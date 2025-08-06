package com.github.muellerma.nfcreader

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.provider.Settings
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.ArgumentMatchers.isNull
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NFCBaseTest {

    @Mock
    lateinit var mockNfcAdapter: NfcAdapter
    @Mock
    lateinit var mockPendingIntent: PendingIntent
    @Mock
    lateinit var mockDialogBuilder: MaterialAlertDialogBuilder
    @Mock
    lateinit var mockIntent: Intent
    @Mock
    lateinit var activity: MainActivity

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(activity.nfcAdapter).thenReturn(mockNfcAdapter)
        //`when`(PendingIntent.getActivity(activity, 0,
          //  Intent(activity, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            //PendingIntent_Mutable)).thenReturn(mockPendingIntent)
    }

    @Test
    fun openNfcSettings() {
        `when`(Intent(Settings.ACTION_NFC_SETTINGS)).thenReturn(mockIntent)
        val method = activity::class.java.getDeclaredMethod("openNfcSettings")
        method.isAccessible = true
        method.invoke(activity)
        verify(activity).startActivity(mockIntent)
    }

   // @Test
    //fun showNoNfcDialog() {
      //  `when`(MaterialAlertDialogBuilder(activity)).thenReturn(mockDialogBuilder)
        //activity.showNoNfcDialog()
        //verify(mockDialogBuilder).show()
   // }

    /*@Test
    fun OnPause_FGDispatch() {
        activity.onPause()
        verify(mockNfcAdapter).disableForegroundDispatch(activity)
    }

    @Test
    fun OnResume_NfcDisabled() {
        `when`(mockNfcAdapter.isEnabled).thenReturn(false)
        activity.onResume()
        verify(activity).openNfcSettings()
    }

    @Test
    fun OnResume_NfcEnabled() {
        `when`(mockNfcAdapter.isEnabled).thenReturn(true)
        activity.onResume()
        verify(mockNfcAdapter).enableForegroundDispatch(eq(activity), eq(mockPendingIntent), isNull(), isNull())
    }*/

}
