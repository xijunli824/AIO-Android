package org.cmaaio.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import org.cmaaio.activity.R;

public class XImageView extends ImageView implements OnTouchListener {
    private ImageCache imageCache = new ImageCache();

    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    private int moveScreenWidth;
    private int moveScreenHeight;
    private int imageWidth;
    private int imageHeight;
    private float minScaleR = 0;// ��С���ű���
    static final float MAX_SCALE = 8f;// ������ű���
    static final int NONE = 0;// ��ʼ״̬
    static final int DRAG = 1;// �϶�
    static final int ZOOM = 2;// ����
    int mode = NONE;
    PointF prev = new PointF();
    PointF mid = new PointF();
    float dist = 1f;
    public boolean isTouch = false;

    public XImageView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        initControls();
    }

    public XImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControls();
    }

    public XImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initControls();
    }

    private void initControls() {
        this.setOnTouchListener(this);
    }

    public void setImageUrl(String url) {
        if (!this.loadFromCache(url)) {
            this.setImageResource(R.drawable.ic_launcher);
        } else {
            LayoutParams lay = this.getLayoutParams();
            lay.height = moveScreenHeight;
            lay.width = moveScreenWidth;
            this.setLayoutParams(lay);
            minZoom();
            center();
            this.setImageMatrix(matrix);
        }
    }

    private boolean loadFromCache(String url) {
        Bitmap cacheBmp = imageCache.getImageFromCache(url);
        if (cacheBmp != null) {
            this.setImageBitmap(cacheBmp);
            imageWidth = cacheBmp.getWidth();
            imageHeight = cacheBmp.getHeight();
            moveScreenWidth = this.getWidth();
            moveScreenHeight = this.getHeight();
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            // ���㰴��
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                prev.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            // ���㰴��
            case MotionEvent.ACTION_POINTER_DOWN:
                dist = spacing(event);
                // �����������������10�����ж�Ϊ���ģʽ
                if (spacing(event) > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - prev.x, event.getY()
                            - prev.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float tScale = newDist / dist;
                        matrix.postScale(tScale, tScale, mid.x, mid.y);
                    }
                }
                break;
        }
        this.setImageMatrix(matrix);
        CheckView();
        return isTouch;
    }

    /**
     * ���������С���ű����Զ�����
     */
    private void CheckView() {
        float p[] = new float[9];
        matrix.getValues(p);
        if (mode == ZOOM) {
            if (p[0] < minScaleR) {
                matrix.setScale(minScaleR, minScaleR);
            }
            if (p[0] > MAX_SCALE) {
                matrix.set(savedMatrix);
            }
        }
        center();
    }

    /**
     * ��С���ű������Ϊ100%
     */
    private void minZoom() {
        minScaleR = Math.min((float) moveScreenWidth / imageWidth,
                (float) moveScreenHeight / imageHeight);
        matrix.postScale(minScaleR, minScaleR);
    }

    private void center() {
        center(true, true);
    }

    /**
     * �����������
     */
    protected void center(boolean horizontal, boolean vertical) {
        Matrix m = new Matrix();
        m.set(matrix);
        RectF rect = new RectF(0, 0, imageWidth, imageHeight);
        // RectF rect = new RectF(0, 0, moveScreenWidth, moveScreenHeight);
        m.mapRect(rect);
        float height = rect.height();
        float width = rect.width();
        float deltaX = 0, deltaY = 0;
        if (vertical) {
            // ͼƬС����Ļ��С���������ʾ��������Ļ���Ϸ������������ƣ��·�������������
            int screenHeight = moveScreenHeight;
            if (height < screenHeight) {
                // deltaY = (screenHeight - height) / 2 - rect.top;
                Log.d("aa",
                        "topandheng==" + rect.top + "--"
                                + this.getHeight());
                if (this.getHeight() == 0) {
                    deltaY = (screenHeight - height) / 2 - rect.top;
                } else {
                    deltaY = (this.getHeight() - height) / 2
                            - rect.top;
                }

            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < screenHeight) {
                deltaY = this.getHeight() - rect.bottom;
            }
        }
        if (horizontal) {
            int screenWidth = moveScreenWidth;
            if (width < screenWidth) {
                deltaX = (screenWidth - width) / 2 - rect.left;
                // deltaX = (lukuangImageView.getWidth() - width) / 2 -
                // rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < screenWidth) {
                deltaX = screenWidth - rect.right;
                // deltaX = lukuangImageView.getWidth() - rect.right;
            }
        }
        matrix.postTranslate(deltaX, deltaY);
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}
