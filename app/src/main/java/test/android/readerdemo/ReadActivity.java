package test.android.readerdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 根据ChaActivity传进来的路径解析显示文本内容
 */
public class ReadActivity extends Activity implements OnClickListener, ReadPagerLayout.TouchListener {

	private String text = "";
	private int textLenght = 0;

	private static final int COUNT = 400;

	private int currentTopEndIndex = 0;

	private int currentShowEndIndex = 0;

	private int currentBottomEndIndex = 0;

	private String code="GB2312";//文本的编码格式，默认是GB2312

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ReadPagerLayout rootLayout = (ReadPagerLayout) findViewById(R.id.container);
			View recoverView = LayoutInflater.from(ReadActivity.this).inflate(R.layout.view_new, null);
			View view1 = LayoutInflater.from(ReadActivity.this).inflate(R.layout.view_new, null);
			View view2 = LayoutInflater.from(ReadActivity.this).inflate(R.layout.view_new, null);
			rootLayout.initFlipperViews(ReadActivity.this, view2, view1, recoverView);

			textLenght = text.length();

			System.out.println("----textLenght----->" + textLenght);

			TextView textView = (TextView) view1.findViewById(R.id.textview);
			if (textLenght > COUNT) {
				textView.setText(text.subSequence(0, COUNT));
				textView = (TextView) view2.findViewById(R.id.textview);
				if (textLenght > (COUNT << 1)) {
					textView.setText(text.subSequence(COUNT, COUNT * 2));
					currentShowEndIndex = COUNT;
					currentBottomEndIndex = COUNT << 1;
				} else {
					textView.setText(text.subSequence(COUNT, textLenght));
					currentShowEndIndex = textLenght;
					currentBottomEndIndex = textLenght;
				}
			} else {
				textView.setText(text.subSequence(0, textLenght));
				currentShowEndIndex = textLenght;
				currentBottomEndIndex = textLenght;
			}
		};
	};
	private String filePath;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read);

		Bundle bunde = this.getIntent().getExtras();
		filePath = bunde.getString("FileName");

		new ReadingThread().start();
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public View createView(final int direction) {
		String txt = "";
		if (direction == ReadPagerLayout.TouchListener.MOVE_TO_LEFT) {
			currentTopEndIndex = currentShowEndIndex;
			final int nextIndex = currentBottomEndIndex + COUNT;
			currentShowEndIndex = currentBottomEndIndex;
			if (textLenght > nextIndex) {
				txt = text.substring(currentBottomEndIndex, nextIndex);
				currentBottomEndIndex = nextIndex;
			} else {
				txt = text.substring(currentBottomEndIndex, textLenght);
				currentBottomEndIndex = textLenght;
			}
		} else {
			currentBottomEndIndex = currentShowEndIndex;
			currentShowEndIndex = currentTopEndIndex;
			currentTopEndIndex = currentTopEndIndex - COUNT;
			txt = text.substring(currentTopEndIndex - COUNT, currentTopEndIndex);
		}

		View view = LayoutInflater.from(this).inflate(R.layout.view_new, null);
		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setText(txt);

//		textView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
//			@Override
//			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//				menu.add(Menu.NONE,1,Menu.NONE,"GB2312");
//				menu.add(Menu.NONE,2,Menu.NONE,"GBK");
//				menu.add(Menu.NONE,3,Menu.NONE,"UTF-8");
//				menu.add(Menu.NONE,4,Menu.NONE,"ANSI");
//				menu.add(Menu.NONE,5,Menu.NONE,"UNICODE");
//			}
//		});

		System.out.println("-top->" + currentTopEndIndex + "-show->" + currentShowEndIndex + "--bottom-->" + currentBottomEndIndex);
		return view;
	}

//	@Override
//	public boolean onContextItemSelected(MenuItem item) {
//		switch (item.getItemId()){
//			case 1:
//				code="GB2312";
//				new ReadingThread().start();
//				break;
//			case 2:
//				code="GBK";
//				new ReadingThread().start();
//				break;
//			case 3:
//				code="UTF-8";
//				new ReadingThread().start();
//				break;
//			case 4:
//				code="ANSI";
//				new ReadingThread().start();
//				break;
//			case 5:
//				code="UNICODE";
//				new ReadingThread().start();
//				break;
//		}
//		return true;
//	}

	@Override
	public boolean whetherHasPreviousPage() {
		return currentShowEndIndex > COUNT;
	}

	@Override
	public boolean whetherHasNextPage() {
		return currentShowEndIndex < textLenght;
	}

	@Override
	public boolean currentIsFirstPage() {
		boolean should = currentTopEndIndex > COUNT;
		if (!should) {
			currentBottomEndIndex = currentShowEndIndex;
			currentShowEndIndex = currentTopEndIndex;
			currentTopEndIndex = currentTopEndIndex - COUNT;
		}
		return should;
	}

	@Override
	public boolean currentIsLastPage() {
		boolean should = currentBottomEndIndex < textLenght;
		if (!should) {
			currentTopEndIndex = currentShowEndIndex;
			final int nextIndex = currentBottomEndIndex + COUNT;
			currentShowEndIndex = currentBottomEndIndex;
			if (textLenght > nextIndex) {
				currentBottomEndIndex = nextIndex;
			} else {
				currentBottomEndIndex = textLenght;
			}
		}
		return should;
	}

	private class ReadingThread extends Thread {
		public void run() {
			StringBuffer sBuffer = new StringBuffer();
			try {
//				Log.i("TAG", "ReadingThread run()"+filePath);
				FileInputStream fInputStream = new FileInputStream(filePath);
				InputStreamReader inputStreamReader = new InputStreamReader(fInputStream,code);
				BufferedReader in = new BufferedReader(inputStreamReader);

				while (in.ready()) {
					sBuffer.append(in.readLine() + "\n");
				}
				in.close();
				text=sBuffer.toString();
				handler.sendEmptyMessage(0);

//				if (in != null) {
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    int i = -1;
//                    while ((i = in.read()) != -1) {
//                        baos.write(i);
//                    }
//                    text = new String(baos.toByteArray(), "UTF-8");
//                    baos.close();
//                    in.close();
//                    fInputStream.close();
//                    handler.sendEmptyMessage(0);
//                }
			} catch (IOException e) {
				e.printStackTrace();
			}

//			FileInputStream fInputStream;
//			try {
////			AssetManager am = getAssets();
//				fInputStream = new FileInputStream(filePath);
////			InputStream response;
////				response = am.open(filePath);
//				if (fInputStream != null) {
//					ByteArrayOutputStream baos = new ByteArrayOutputStream();
//					int i = -1;
//					while ((i = fInputStream.read()) != -1) {
//						baos.write(i);
//					}
//					text = new String(baos.toByteArray(), "UTF-8");
//					baos.close();
//					fInputStream.close();
//					handler.sendEmptyMessage(0);
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}

		}
	}

//	private String fromInputStreamToString(FileInputStream fis){
//		ByteArrayOutputStream baos=new ByteArrayOutputStream();
//		byte[] buffer=new byte[1024];
//		int len;
//		while(len=fis.read(buffer)){
//			baos.write(buffer,0,len);
//		}
//		return baos.toString();
//	}

}
