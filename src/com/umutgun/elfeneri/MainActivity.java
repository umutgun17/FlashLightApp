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

		// layout ile aç kapa image butonunu koda baðlýyoruz.
		btnAcKapa = (ImageButton) findViewById(R.id.btnSwitch);
		
		// parlaklýðý azalttýp çoðaltmak için kullandýðým component
		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		seekBar.setMax(255);
		
		// kamera getir methodunu çaðarýr
		kameraGetir();

		
		// btnAcKapa image buttonuna týklandýðýnda flashý açma, kapatma methodu
		btnAcKapa.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (flashAcik) {
					// flash açýksa ife girer ve flashKapat metodunu çalýþtýrýr
					flashKapat();
				} else {
					// flash kapalýysa else kýsmýna girer ve flashAc metodunu çalýþtýtýr
					flashAc();
				}
			}
		});
		
		// telefonun aktif olan parlaklýk deðerini çekerek parlaklikDegeri deðiþkenine atýyor.
		try {
			parlaklikDegeri = android.provider.Settings.System.getInt(getContentResolver(),
					android.provider.Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}

		// çektiðimiz deðeri seekbara atýyoruz.
		int ekranParlakligi = (int) parlaklikDegeri;
		seekBar.setProgress(ekranParlakligi);
		// seekbar deðiþtiðinde çalýþýcak method
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int ilerleme = 0;

			// seekbardaki deðeri ilerleme deðiþkeneni atýyoruz.
			@Override
			public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
				ilerleme = progresValue;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {	}

			// deðeri deðiþtirme iþimiz bittiði zaman deðiþime göre ekran parlaklýðý ayarý yapýlýr.
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				android.provider.Settings.System.putInt(getContentResolver(),
						android.provider.Settings.System.SCREEN_BRIGHTNESS, ilerleme);
			}
		});
	}

	// var olan kamera henüz çekilmediyse kamerayý çeker
	private void kameraGetir() {
		if (camera == null) {
			try {
				camera = Camera.open();
				params = camera.getParameters();
			} catch (RuntimeException e) {
				Log.e("Kamera Hatasý. Kamera Açýlamadý..", e.getMessage());
			}
		}
	}

	// flashAc metodu içerisinde flash açýk deðilse flashýn açýlmasý saðlanýyor
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

			// flash açýldýðý için imagebuttondaki resmin deðiþtirmesini saðlayan method çaðrýlýyor
			buttonResimDegistir();
		}
	}

	// flashKapat metodu içerisinde flash açýk ise flashýn kapanmasýný saðlanýyor
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

			// flash kapandýðý için imagebuttondaki resmin deðiþtirmesini saðlayan method çaðrýlýyor
			buttonResimDegistir();
		}
	}

	// flash açýk olup olmadýðýný kontrol ederek ona göre ampül resminin deðiþmesini saðlayan method
	private void buttonResimDegistir() {
		if (flashAcik) {
			btnAcKapa.setImageResource(R.drawable.acik_btn);
		} else {
			btnAcKapa.setImageResource(R.drawable.kapali_btn);
		}
	}
}
