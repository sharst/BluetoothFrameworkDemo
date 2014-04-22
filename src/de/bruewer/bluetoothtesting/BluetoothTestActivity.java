package de.bruewer.bluetoothtesting;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import de.uos.nbp.bluetoothconnectiondemo.R;
import de.uos.nbp.senhance.bluetooth.DeviceListActivity;
import de.uos.nbp.senhance.bluetooth.FramedPacketConnection;
import de.uos.nbp.senhance.bluetooth.PacketConnection.Packet;
import de.uos.nbp.senhance.bluetooth.PacketConnectionHandler;

public class BluetoothTestActivity extends Activity {

	private final static String TAG = "TestActivity";
	private String mAddress;
	private FramedPacketConnection mBluetoothPacketConnection;
	private TextView tv;
	private TextView tvAddr;
	private TextView tvRecv;
	private boolean autoReconnect = true;
	
	// Finals	
	public static final int REQUEST_DEVICE_ADDRESS = 1;
	
	public static final int PORT = 3;   // 1 for belt, 3 for BeltSimulator

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testactivity);
		tv = (TextView) findViewById(R.id.sentText);
		tvAddr = (TextView) findViewById(R.id.dev_address);
		tvRecv = (TextView) findViewById(R.id.recvText);
	}
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_DEVICE_ADDRESS:
				mAddress = data.getStringExtra(DeviceListActivity.INTENT_DEVICEADDRESS);
				tvAddr.setText(mAddress);
				openConnection();
				break;
			}
		}
	}
	
	public void sendFloatArray(View v) {
		Random rand = new Random();
		// Create a randomly sized array
		float[] mArray = new float[rand.nextInt(10)+1];
		
		for (int ii = 0; ii<mArray.length; ii++) {
			// Fill array with random floats
			mArray[ii] = rand.nextFloat()*100;
		}
		tv.setText(Arrays.toString(mArray));
		sendPacket(new Packet(mArray));
	}
	
	public void sendIntArray(View v) {

		Random rand = new Random();
		// Create a randomly sized array
		int[] mArray = new int[rand.nextInt(10)+1];
		
		for (int ii = 0; ii<mArray.length; ii++) {
			// Fill array with random ints
			mArray[ii] = rand.nextInt(255);
		}
		tv.setText(Arrays.toString(mArray));
		sendPacket(new Packet(mArray));
	}
	
	public void searchDevices(View v) {
		Intent intent = new Intent(this, de.uos.nbp.senhance.bluetooth.DeviceListActivity.class);
		startActivityForResult(intent, REQUEST_DEVICE_ADDRESS);
	}
	
	
	public void sendPacket(Packet pckt) {
		if (mAddress==null) {
			Toast.makeText(this, "Please select a device first!", Toast.LENGTH_LONG).show();
			return;
		}
		if (mBluetoothPacketConnection.isConnected()) {
					
			try {
				Log.d(TAG, "Sending packet!");
				mBluetoothPacketConnection.send(pckt);
			} catch (IOException e) {
				Log.e(TAG, "Could not send to connection handler");
			}
		} else {
			Log.d(TAG, "Sorry, not yet connected. Trying again!");
			mBluetoothPacketConnection.connect(PORT);
		}
	}
	
	public void openConnection() {
		
		mBluetoothPacketConnection = new FramedPacketConnection(mAddress, mConnectionHandler, 20, -1, 1000);
		mBluetoothPacketConnection.connect(PORT);
		
	}
	
    public void closeConnection(){
    	if (mBluetoothPacketConnection!=null){
	    	mBluetoothPacketConnection.close();
    	}
    }


	private final PacketConnectionHandler mConnectionHandler = new PacketConnectionHandler() {
		public void packetReceived(Packet receivedPacket) {
			//Toast.makeText(getApplicationContext(), "Received a packet!", Toast.LENGTH_SHORT).show();
			tvRecv.setText(Arrays.toString(receivedPacket.getFloatArray()));
		}
		
		public void connectionLost(String message) {
			Toast.makeText(getApplicationContext(), "Connection lost!", Toast.LENGTH_SHORT).show();
			if (autoReconnect) openConnection();
		}

		public void connectionClosed() {
			Toast.makeText(getApplicationContext(), "Connection closed.", Toast.LENGTH_SHORT).show();
		}

		public void connected() {
			Toast.makeText(getApplicationContext(), "Connected!", Toast.LENGTH_SHORT).show();
		}

		public void connectFailed(String message) {
			Toast.makeText(getApplicationContext(), "Could not connect!", Toast.LENGTH_SHORT).show();
		}

		public void connectAttemptFailed(String message) {
			Toast.makeText(getApplicationContext(), "Connection attempt failed!", Toast.LENGTH_SHORT).show();
		}
	};
}
