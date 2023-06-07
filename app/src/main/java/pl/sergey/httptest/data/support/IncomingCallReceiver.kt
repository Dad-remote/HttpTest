package pl.sergey.httptest.data.support

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import dagger.hilt.android.AndroidEntryPoint
import pl.sergey.httptest.domain.repository.CallLogRepository
import javax.inject.Inject

@AndroidEntryPoint
class IncomingCallReceiver : BroadcastReceiver() {

    @Inject lateinit var callNumberHolder: CallDataHolder
    @Inject lateinit var callLogRepository: CallLogRepository

    companion object {
        private fun getContactName(context: Context, number: String) : String {
            val projection = arrayOf(
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup.NUMBER,
                ContactsContract.PhoneLookup.HAS_PHONE_NUMBER
            )
            val contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
            val cursor = context.contentResolver.query(contactUri, projection, null, null, null);
            val contactName = if (cursor?.moveToFirst() == true) {
                cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME))
            } else {
                ""
            }
            cursor?.close()
            return contactName
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            val phoneNumber = intent?.extras?.getString(Intent.EXTRA_PHONE_NUMBER)
            callNumberHolder.push(phoneNumber ?: "", phoneNumber?.let { getContactName(context!!, it) } ?: "")
        } else {
            val telephony = context!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            telephony.listen(CallListener(context, callNumberHolder), PhoneStateListener.LISTEN_CALL_STATE)
        }
        callLogRepository.getLog()
    }

    class CallListener(private val context: Context, private val callNumberHolder: CallDataHolder) : PhoneStateListener() {

        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            if (phoneNumber?.isNotEmpty() == true) {
                callNumberHolder.push(phoneNumber, getContactName(context, phoneNumber))
            }
        }

    }
}