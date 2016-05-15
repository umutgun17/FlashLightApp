package com.umutgun.elfeneri;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity {

	private ImageButton btnAcKapa;
	private SeekBar seekBar;
	private Camera camera;
	private boolean flashAcik;
	Parameters params;
	MediaPlayer mp;
	float parlaklikDegeri = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// layout ile a� kapa image butonunu koda ba�l�yoruz.
		btnAcKapa = (ImageButton) findViewById(R.id.btnSwitch);
		
		// parlakl��� azaltt�p �o�altmak i�in kulland���m component
		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		seekBar.setMax(255);
		
		// kamera getir methodunu �a�ar�r
		kameraGetir();

		
		// btnAcKapa image buttonuna t�kland���nda flash� a�ma, kapatma methodu
		btnAcKapa.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (flashAcik) {
					// flash a��ksa ife girer ve flashKapat metodunu �al��t�r�r
					flashKapat();
				} else {
					// flash kapal�ysa else k�sm�na girer ve flashAc metodunu �al��t�t�r
					flashAc();
				}
			}
		});
		
		// telefonun aktif olan parlakl�k de�erini �ekerek parlaklikDegeri de�i�kenine at�yor.
		try {
			parlaklikDegeri = android.provider.Settings.System.getInt(getContentResolver(),
					android.provider.Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}

		// �ekti�imiz de�eri seekbara at�yoruz.
		int ekranParlakligi = (int) parlaklikDegeri;
		seekBar.setProgress(ekranParlakligi);
		// seekbar de�i�ti�inde �al���cak method
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int ilerleme = 0;

			// seekbardaki de�eri ilerleme de�i�keneni at�yoruz.
			@Override
			public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
				ilerleme = progresValue;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {	}

			// de�eri de�i�tirme i�imiz bitti�i zaman de�i�ime g�re ekran parlakl��� ayar� yap�l�r.
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				android.provider.Settings.System.putInt(getContentResolver(),
						android.provider.Settings.System.SCREEN_BRIGHTNESS, ilerleme);
			}
		});
	}

	// var olan kamera hen�z �ekilmediyse kameray� �eker
	private void kameraGetir() {
		if (camera == null) {
			try {
				camera = Camera.open();
				params = camera.getParameters();
			} catch (RuntimeException e) {
				Log.e("Kamera Hatas�. Kamera A��lamad�..", e.getMessage());
			}
		}
	}

	// flashAc metodu i�erisinde flash a��k de�ilse flash�n a��lmas� sa�lan�yor
	private void flashAc() {
		if (!flashAcik) {
			if (camera == null || params == null) {
				return;
			}

			params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_TORCH);
			camera.setParameters(params);
			camera.startPreview();
			flashAcik = true;

			// flash a��ld��� i�in imagebuttondaki resmin de�i�tirmesini sa�layan method �a�r�l�yor
			buttonResimDegistir();
		}
	}

	// flashKapat metodu i�erisinde flash a��k ise flash�n kapanmas�n� sa�lan�yor
	private void flashKapat() {
		if (flashAcik) {
			if (camera == null || params == null) {
				return;
			}

			params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_OFF);
			camera.setParameters(params);
			camera.stopPreview();
			flashAcik = false;

			// flash kapand��� i�in imagebuttondaki resmin de�i�tirmesini sa�layan method �a�r�l�yor
			buttonResimDegistir();
		}
	}

	// flash a��k olup olmad���n� kontrol ederek ona g�re amp�l resminin de�i�mesini sa�layan method
	private void buttonResimDegistir() {
		if (flashAcik) {
			btnAcKapa.setImageResource(R.drawable.acik_btn);
		} else {
			btnAcKapa.setImageResource(R.drawable.kapali_btn);
		}
	}
}
