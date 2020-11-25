package com.example.geologger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by hkf on 2016/8/8.
 * 自定义星空图控件
 */
public class StarMapView extends View {
	//区别不同系统系统卫星的画笔
	private Paint markerPaint;
	private Paint markerPaint1;
	//	private Paint markerPaint2;
	//	private Paint markerPaint3;
	//	private Paint markerPaint4;
	//	private Paint markerPaint5;

	//字体画笔
	private Paint textPaint;
	private Paint textPaint1;

	//高度角圆环画笔
	private Paint circlePaint;
	/*高度角限制圈的画笔*/
	private Paint circlePaint1;

	//N,E,W,S
	private String northString;
	private String eastString;
	private String southString;
	private String westString;

	//Text高度
	private int textHeight;

	// 高度角，方位角数组，卫星编号数组
	private double[] Altitude;
	private double[] Azimuth;
	private int[] PRN;
	private float[] SNR;

	//BD2, GPS, SBAS, GLON是否勾选判断
	private boolean BD2, BD3, GPS, SBAS, GLON, QZSS, GALILEO;

	private Bitmap mGpsBitmap; // GPS卫星
	private Bitmap mSbasBitmap; // Sbas卫星
	private Bitmap mGloBitmap; // Glonass卫星
	private Bitmap mBdsBitmap; // BD2卫星
	private Bitmap mGalBitmap; // Galileo卫星
	private Bitmap mQzssBitmap; // QZSS卫星

	// 高度截止角
	private int Angle;
	private String str_Angle;

	public StarMapView(Context context) {
		super(context);
		initStarMap();
	}

	public StarMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initStarMap();
	}

	public StarMapView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initStarMap();
	}

	/**
	 * 外部调用，用来显示卫星方位
	 * @param Alt 高度角数组
	 * @param Azi 方位角数组
	 * @param Prn 卫星号数组
	 */
	public void setMarker(double[] Alt, double[] Azi, int[] Prn, float[] SNR) {
		this.Altitude = Alt;
		this.Azimuth = Azi;
		this.PRN = Prn;
		this.SNR = SNR;
		//添加完毕后刷新视图
		this.invalidate();
	}

	/**
	 * 外部调用，用来筛选显示的卫星系统
	 * @param BD2 是否勾选
	 * @param GPS 是否勾选
	 * @param SBAS 是否勾选
	 * @param GLON 是否勾选
	 */
	public void showSource(boolean BD2, boolean BD3, boolean GPS,
			boolean SBAS, boolean GLON, boolean QZSS, boolean GALILEO) {
		this.BD2 = BD2;
		this.BD3 = BD3;
		this.GLON = GLON;
		this.GPS = GPS;
		this.SBAS = SBAS;
		this.QZSS = QZSS;
		this.GALILEO = GALILEO;
		//设置完毕后刷新视图
		this.invalidate();
	}

	/**
	 * 初始化星空图绘制参数
	 */
	private void initStarMap() {
		setFocusable(true);

		//高度截止角默认初始值为0
		Angle = 0;

		//卫星系统默认4个系统全部显示
		BD2 = true;
		GPS = true;
		GLON = true;
		SBAS = true;
		QZSS = true;
		GALILEO = true;

		//设置高度角圆圈绘制画笔参数，颜色为灰色
		circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaint.setColor(Color.GRAY);
		circlePaint.setStrokeWidth(2); // 笔画宽度
		circlePaint.setStyle(Paint.Style.STROKE);

		//设置高度截止角圆圈绘制画笔参数，颜色为红色
		circlePaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaint1.setColor(Color.RED);
		circlePaint1.setStrokeWidth(2); // 笔画宽度
		circlePaint1.setStyle(Paint.Style.STROKE);

		//4个指向
		northString = "N";
		eastString = "E";
		southString = "S";
		westString = "W";

		//字体画笔，颜色为黑色，字体大小此处注释掉，draw的时候根据具体屏幕尺寸具体设置
		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setColor(Color.BLACK);
		// textPaint.setTextSize(18);

		textPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint1.setColor(Color.BLACK);
		// textPaint1.setTextSize(23);

		//获取下移高度
		textHeight = (int) textPaint.measureText("yY");

		//4套不同系统卫星的marker，分别设置画笔
		/*GPS红色*/
		markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		markerPaint.setColor(Color.RED);
		markerPaint.setStrokeWidth(1); // 笔画宽度
		markerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		/*SBAS蓝色*/
		markerPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
		markerPaint1.setColor(Color.BLUE);
		markerPaint1.setStrokeWidth(1); // 笔画宽度
		markerPaint1.setStyle(Paint.Style.FILL_AND_STROKE);
		//		/*GLONASS青色*/
		//		markerPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
		//		markerPaint2.setColor(Color.CYAN);
		//		markerPaint2.setStrokeWidth(1); // 笔画宽度
		//		markerPaint2.setStyle(Paint.Style.FILL_AND_STROKE);
		//		/*BD2绿色*/
		//		markerPaint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
		//		markerPaint3.setColor(Color.GREEN);
		//		markerPaint3.setStrokeWidth(1); // 笔画宽度
		//		markerPaint3.setStyle(Paint.Style.FILL_AND_STROKE);
		//		/*QZSS黑色*/
		//		markerPaint4 = new Paint(Paint.ANTI_ALIAS_FLAG);
		//		markerPaint4.setColor(Color.BLACK);
		//		markerPaint4.setStrokeWidth(1); // 笔画宽度
		//		markerPaint4.setStyle(Paint.Style.FILL_AND_STROKE);
		//		/*GALILEO灰色*/
		//		markerPaint5 = new Paint(Paint.ANTI_ALIAS_FLAG);
		//		markerPaint5.setColor(Color.GRAY);
		//		markerPaint5.setStrokeWidth(1); // 笔画宽度
		//		markerPaint5.setStyle(Paint.Style.FILL_AND_STROKE);

		mGalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gal);
		mGpsBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gps);
		mGloBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.glo);
		mQzssBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.qzs);
		mSbasBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sbas);
		mBdsBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bds);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measuredWidth = measure(widthMeasureSpec);
		int measuredHeight = measure(heightMeasureSpec);
		//取屏幕宽高的最小值作为画布边长
		int d = Math.min(measuredWidth, measuredHeight);
		setMeasuredDimension(d, d);
	}

	/**
	 * 获取屏幕尺寸
	 * @param measureSpec
	 * @return 尺寸
	 */
	protected int measure(int measureSpec) {
		int result;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.UNSPECIFIED) {
			//未指定则赋值为200
			result = 200;
		} else {
			result = specSize;
		}
		return result;
	}

	/**
	 * 获取绘制圆圈的大小
	 * @return 半径大小
	 */
	private float getRadius() {
		int mMeasuredWidth = getMeasuredWidth();
		int mMeasuredHeight = getMeasuredHeight();
		int px = mMeasuredWidth / 2;
		int py = mMeasuredHeight / 2;
		return Math.min(px, py);
	}


	@Override
	protected void onDraw(Canvas canvas) {
		//获取画布宽度
		int mMeasuredWidth = getMeasuredWidth();
		int mMeasuredHeight = getMeasuredHeight();

		//setMeasuredDimension(d, d)之后px=py，-5是为了留一些空间
		int px = mMeasuredWidth / 2 - 5;
		int py = mMeasuredHeight / 2 - 5;

		//确定高度角圆圈半径(0,30,60各画一个圈圈)
		float radius = Math.min(px, py);
		float radius1 = radius / 3;
		float radius2 = radius1 * 2;

		//根据半径大小设置字体大小
		textPaint.setTextSize(getRadius() / 10);
		textPaint1.setTextSize(getRadius() / 14);

		// 绘制高度角圈圈
		canvas.drawCircle(px, py, radius, circlePaint);
		canvas.drawCircle(px, py, radius1, circlePaint);
		canvas.drawCircle(px, py, radius2, circlePaint);
		canvas.drawLine(px, py - radius, px, py + radius, circlePaint);
		canvas.drawLine(px - radius, py, px + radius, py, circlePaint);
		canvas.save();

		//字体宽度获取后便于居中显示
		int textWidth_W = (int) textPaint.measureText("W");
		int textHeight_W = (int) textPaint.measureText("W");
		int textWidth_N = (int) textPaint.measureText("N");
		int textHeight_N = (int) textPaint.measureText("N");
		int textWidth_E = (int) textPaint.measureText("E");
		int textHeight_E = (int) textPaint.measureText("E");
		int textWidth_S = (int) textPaint.measureText("S");
		int textHeight_S = (int) textPaint.measureText("S");
		int textWidth_0 = (int) textPaint.measureText("0");

		//NEWS绘制方位
		int cardinalWX = px - textWidth_W / 2;
		int cardinalWY = py - (int) radius + textHeight_W / 2;
		int cardinalNX = px - textWidth_N / 2;
		int cardinalNY = py - (int) radius + textHeight_W / 2;
		int cardinalEX = px - textWidth_E / 2;
		int cardinalEY = py - (int) radius + textHeight_W / 2;
		int cardinalSX = px - textWidth_S / 2;
		int cardinalSY = py - (int) radius + textHeight_W / 2;

		//绘制小度盘，每45度标记
		for (int i = 0; i < 24; i++) {
			// 360度划分成24格，15度一格
			canvas.drawLine(px, py - radius, px, py - radius + radius / 30, markerPaint);
			canvas.save();
			canvas.translate(0, textHeight);
			if (i % 6 == 0) {
				String dirString = "";
				switch (i) {
				case (0): {
					dirString = northString;
					canvas.drawText(dirString, cardinalNX, cardinalNY, textPaint);
					//高度角标识30,60,90
					String str30 = "30°";
					String str60 = "60°";
					String str90 = "90°";
					canvas.drawText(str30, px + textWidth_0, radius1, textPaint);
					canvas.drawText(str60, px + textWidth_0, radius2, textPaint);
					canvas.drawText(str90, px + textWidth_0, radius, textPaint);
					break;
				}
				case (6):
					dirString = eastString;
					canvas.drawText(dirString, cardinalEX, cardinalEY, textPaint);
					break;
				case (12):
					dirString = southString;
					canvas.drawText(dirString, cardinalSX, cardinalSY, textPaint);
					break;
				case (18):
					dirString = westString;
					canvas.drawText(dirString, cardinalWX, cardinalWY, textPaint);
					break;
				}
			} else if (i % 3 == 0) {
				String angle = String.valueOf(i * 15);
				float angleTextWidth = textPaint.measureText(angle);
				int angleTextX = (int) (px - angleTextWidth / 2);
				int angleTextY = py - (int) radius + (textHeight_W / 2);
				canvas.drawText(angle, angleTextX, angleTextY, textPaint);
				canvas.drawLine(px, py - radius, px, py, circlePaint);
			}
			canvas.restore();
			canvas.rotate(15, px, py);
		}
		canvas.restore();
		//		canvas.save();

		// 绘制高度角限制圈
		if (!TextUtils.isEmpty(str_Angle)) {
			float radius3 = radius - ((float) (Angle) / 90) * radius;
			canvas.drawCircle(px, py, radius3, circlePaint1);
		}

		if ((Altitude != null) && (Azimuth != null)) {
			for (int i = 0; i < Altitude.length; i++) {
				if (Altitude[i] >= Angle && SNR[i] > 0) {
					float setAlt = (float) (Altitude[i] / 90) * radius;
					float setAzi = (float) Azimuth[i];
					int setPrn = PRN[i];
					canvas.save();
					//绘制时首先旋转对应方位角，然后按高度角对应90度的比例对应半径绘制方位，之后再旋转回来
					canvas.rotate(setAzi, px, py);
					//不同系统的卫星进行区分
					if ((setPrn >= 1) && (setPrn <= 32) && GPS) {
						//canvas.drawCircle(px, py - radius + setAlt, radius / 30, markerPaint);
						canvas.rotate(-setAzi, px, py - radius + setAlt);
						canvas.drawBitmap(mGpsBitmap, px, py - radius + setAlt, markerPaint);
						canvas.drawText("G" + setPrn, px + radius / 60, py - radius + setAlt, textPaint1);

					} else if ((setPrn >= 65) && (setPrn <= 96) && GLON) {
						//canvas.drawCircle(px, py - radius + setAlt, radius / 30, markerPaint2);
						canvas.rotate(-setAzi, px, py - radius + setAlt);
						canvas.drawBitmap(mGloBitmap, px, py - radius + setAlt, markerPaint);
						canvas.drawText("R" + (setPrn - 64), px + radius / 60, py - radius + setAlt, textPaint1);

					} else if ((setPrn >= 120) && (setPrn < 155) && SBAS) {
						//canvas.drawCircle(px, py - radius + setAlt, radius / 30, markerPaint1);
						canvas.rotate(-setAzi, px, py - radius + setAlt);
						canvas.drawBitmap(mSbasBitmap, px, py - radius + setAlt, markerPaint);
						canvas.drawText("S" + (setPrn - 119), px + radius / 60, py - radius + setAlt, textPaint1);

					} else if ((setPrn >= 183) && (setPrn < 192) && SBAS) {
						//canvas.drawCircle(px, py - radius + setAlt, radius / 30, markerPaint1);
						canvas.rotate(-setAzi, px, py - radius + setAlt);
						canvas.drawBitmap(mSbasBitmap, px, py - radius + setAlt, markerPaint);
						canvas.drawText("S" + (setPrn - 182), px + radius / 60, py - radius + setAlt, textPaint1);

					} else if ((setPrn >= 193) && (setPrn <= 200) && QZSS) {
						//canvas.drawCircle(px, py - radius + setAlt, radius / 30, markerPaint4);
						canvas.rotate(-setAzi, px, py - radius + setAlt);
						canvas.drawBitmap(mQzssBitmap, px, py - radius + setAlt, markerPaint);
						canvas.drawText("Q" + (setPrn - 192), px + radius / 60, py - radius + setAlt, textPaint1);

					} else if ((setPrn >= 201) && (setPrn <= 216) && BD2) {
						//markerPaint3.setColor(Color.YELLOW);
						//canvas.drawCircle(px, py - radius + setAlt, radius / 30, markerPaint3);
						canvas.rotate(-setAzi, px, py - radius + setAlt);
						canvas.drawBitmap(mBdsBitmap, px, py - radius + setAlt, markerPaint);
						canvas.drawText("C" + (setPrn - 200), px + radius / 60, py - radius + setAlt, textPaint1);

					} else if ((setPrn >= 217) && (setPrn <= 237) && BD3) {
						//markerPaint3.setColor(Color.GREEN);
						//canvas.drawCircle(px, py - radius + setAlt, radius / 30, markerPaint3);
						canvas.rotate(-setAzi, px, py - radius + setAlt);
						canvas.drawBitmap(mBdsBitmap, px, py - radius + setAlt, markerPaint);
						canvas.drawText("C" + (setPrn - 200), px + radius / 60, py - radius + setAlt, textPaint1);

					} else if ((setPrn >= 301) && (setPrn <= 340) && GALILEO) {
						//canvas.drawCircle(px, py - radius + setAlt, radius / 30, markerPaint5);
						canvas.rotate(-setAzi, px, py - radius + setAlt);
						canvas.drawBitmap(mGalBitmap, px, py - radius + setAlt, markerPaint);
						canvas.drawText("E" + (setPrn - 300), px + radius / 60, py - radius + setAlt, textPaint1);
					}
					canvas.restore();
					//canvas.save();
				}
			}
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mSbasBitmap.recycle();
		mQzssBitmap.recycle();
		mGpsBitmap.recycle();
		mGloBitmap.recycle();
		mBdsBitmap.recycle();
		mGalBitmap.recycle();
	}
}
