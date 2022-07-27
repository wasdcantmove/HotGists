package com.example.hotgists.app.util.view

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import java.util.*


class PinchImageView : androidx.appcompat.widget.AppCompatImageView {
    private var mOnClickListener: OnClickListener? = null

    private var mOnLongClickListener: OnLongClickListener? = null
    override fun setOnClickListener(l: OnClickListener?) {
        mOnClickListener = l
    }

    override fun setOnLongClickListener(l: OnLongClickListener?) {
        mOnLongClickListener = l
    }

    private val mOuterMatrix: Matrix = Matrix()

    private var mMask: RectF? = null
    var pinchMode: Int = PINCH_MODE_FREE
        private set

    fun getOuterMatrix(matrix: Matrix?): Matrix {
        var matrix: Matrix? = matrix
        if (matrix == null) {
            matrix = Matrix(mOuterMatrix)
        } else {
            matrix.set(mOuterMatrix)
        }
        return matrix
    }

    private fun getInnerMatrix(matrix: Matrix?): Matrix {
        var matrix: Matrix? = matrix
        if (matrix == null) {
            matrix = Matrix()
        } else {
            matrix.reset()
        }
        if (isReady) {

            val tempSrc: RectF = MathUtils.rectFTake(
                0f,
                0f,
                drawable.intrinsicWidth.toFloat(),
                drawable.intrinsicHeight.toFloat()
            )!!

            val tempDst: RectF? = MathUtils.rectFTake(0f, 0f, width.toFloat(), height.toFloat())

            matrix.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.CENTER)

            tempDst?.let { MathUtils.rectFGiven(it) }
            MathUtils.rectFGiven(tempSrc)
        }
        return matrix
    }

    private fun getCurrentImageMatrix(matrix: Matrix): Matrix {
        var matrix: Matrix = matrix
        matrix = getInnerMatrix(matrix)

        matrix.postConcat(mOuterMatrix)
        return matrix
    }

    fun getImageBound(rectF: RectF?): RectF {
        var rectF: RectF? = rectF
        if (rectF == null) {
            rectF = RectF()
        } else {
            rectF.setEmpty()
        }
        if (!isReady) {
            return rectF
        } else {

            val matrix: Matrix? = MathUtils.matrixTake()

            matrix?.let { getCurrentImageMatrix(it) }

            rectF.set(0f, 0f, drawable.intrinsicWidth.toFloat(), drawable.intrinsicHeight.toFloat())
            matrix?.mapRect(rectF)

            matrix?.let { MathUtils.matrixGiven(it) }
            return rectF
        }
    }

    val mask: RectF?
        get() {
            return if (mMask != null) {
                RectF(mMask)
            } else {
                null
            }
        }

    override fun canScrollHorizontally(direction: Int): Boolean {
        if (pinchMode == PINCH_MODE_SCALE) {
            return true
        }
        val bound: RectF = getImageBound(null) ?: return false
        if (bound.isEmpty) {
            return false
        }
        return if (direction > 0) {
            bound.right > width
        } else {
            bound.left < 0
        }
    }

    override fun canScrollVertically(direction: Int): Boolean {
        if (pinchMode == PINCH_MODE_SCALE) {
            return true
        }
        val bound: RectF = getImageBound(null) ?: return false
        if (bound.isEmpty) {
            return false
        }
        return if (direction > 0) {
            bound.bottom > height
        } else {
            bound.top < 0
        }
    }

    fun outerMatrixTo(endMatrix: Matrix?, duration: Long) {
        if (endMatrix == null) {
            return
        }

        pinchMode = PINCH_MODE_FREE

        cancelAllAnimator()

        if (duration <= 0) {
            mOuterMatrix.set(endMatrix)
            dispatchOuterMatrixChanged()
            invalidate()
        } else {

            mScaleAnimator = ScaleAnimator(mOuterMatrix, endMatrix, duration)
            mScaleAnimator?.start()
        }
    }

    fun zoomMaskTo(mask: RectF?, duration: Long) {
        if (mask == null) {
            return
        }

        if (mMaskAnimator != null) {
            mMaskAnimator?.cancel()
            mMaskAnimator = null
        }

        if (duration <= 0 || mMask == null) {
            if (mMask == null) {
                mMask = RectF()
            }
            mMask?.set(mask)
            invalidate()
        } else {

            mMaskAnimator = MaskAnimator(mMask!!, mask, duration)
            mMaskAnimator?.start()
        }
    }

    fun reset() {

        mOuterMatrix.reset()
        dispatchOuterMatrixChanged()

        mMask = null

        pinchMode = PINCH_MODE_FREE
        mLastMovePoint.set(0f, 0f)
        mScaleCenter.set(0f, 0f)
        mScaleBase = 0f

        if (mMaskAnimator != null) {
            mMaskAnimator?.cancel()
            mMaskAnimator = null
        }
        cancelAllAnimator()

        invalidate()
    }

    interface OuterMatrixChangedListener {
        fun onOuterMatrixChanged(pinchImageView: PinchImageView?)
    }

    private var mOuterMatrixChangedListeners: MutableList<OuterMatrixChangedListener>? = null

    private var mOuterMatrixChangedListenersCopy: MutableList<OuterMatrixChangedListener>? = null

    private var mDispatchOuterMatrixChangedLock: Int = 0

    fun addOuterMatrixChangedListener(listener: OuterMatrixChangedListener?) {
        if (listener == null) {
            return
        }

        if (mDispatchOuterMatrixChangedLock == 0) {
            if (mOuterMatrixChangedListeners == null) {
                mOuterMatrixChangedListeners = ArrayList()
            }
            mOuterMatrixChangedListeners?.add(listener)
        } else {


            if (mOuterMatrixChangedListenersCopy == null) {
                mOuterMatrixChangedListenersCopy = if (mOuterMatrixChangedListeners != null) {
                    ArrayList(mOuterMatrixChangedListeners)
                } else {
                    ArrayList()
                }
            }
            mOuterMatrixChangedListenersCopy?.add(listener)
        }
    }

    fun removeOuterMatrixChangedListener(listener: OuterMatrixChangedListener?) {
        if (listener == null) {
            return
        }

        if (mDispatchOuterMatrixChangedLock == 0) {
            if (mOuterMatrixChangedListeners != null) {
                mOuterMatrixChangedListeners?.remove(listener)
            }
        } else {


            if (mOuterMatrixChangedListenersCopy == null) {
                if (mOuterMatrixChangedListeners != null) {
                    mOuterMatrixChangedListenersCopy = ArrayList(mOuterMatrixChangedListeners)
                }
            }
            if (mOuterMatrixChangedListenersCopy != null) {
                mOuterMatrixChangedListenersCopy?.remove(listener)
            }
        }
    }

    private fun dispatchOuterMatrixChanged() {
        if (mOuterMatrixChangedListeners == null) {
            return
        }



        mDispatchOuterMatrixChangedLock++

        mOuterMatrixChangedListeners?.forEach { listener: OuterMatrixChangedListener ->
            listener.onOuterMatrixChanged(this)
        }

        mDispatchOuterMatrixChangedLock--

        if (mDispatchOuterMatrixChangedLock == 0) {

            if (mOuterMatrixChangedListenersCopy != null) {

                mOuterMatrixChangedListeners = mOuterMatrixChangedListenersCopy

                mOuterMatrixChangedListenersCopy = null
            }
        }
    }

    private fun calculateNextScale(innerScale: Float, outerScale: Float): Float {
        val currentScale: Float = innerScale * outerScale
        return if (currentScale < maxScale) {
            maxScale
        } else {
            innerScale
        }
    }


    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initView()
    }

    private fun initView() {

        super.setScaleType(ScaleType.MATRIX)
    }


    override fun setScaleType(scaleType: ScaleType) {}


    override fun onDraw(canvas: Canvas) {

        if (isReady) {
            val matrix: Matrix? = MathUtils.matrixTake()
            imageMatrix = matrix?.let { getCurrentImageMatrix(it) }
            matrix?.let { MathUtils.matrixGiven(it) }
        }

        if (mMask != null) {
            canvas.save()
            canvas.clipRect(mMask!!)
            super.onDraw(canvas)
            canvas.restore()
        } else {
            super.onDraw(canvas)
        }
    }

    private val isReady: Boolean
        get() = (drawable != null) && (drawable.intrinsicWidth > 0) && (drawable.intrinsicHeight > 0
                ) && (width > 0) && (height > 0)

    private var mMaskAnimator: MaskAnimator? = null

    private inner class MaskAnimator(start: RectF, end: RectF, duration: Long) :
        ValueAnimator(), AnimatorUpdateListener {
        private val mStart: FloatArray = FloatArray(4)
        private val mEnd: FloatArray = FloatArray(4)
        private val mResult: FloatArray = FloatArray(4)
        override fun onAnimationUpdate(animation: ValueAnimator) {

            val value: Float = animation.animatedValue as Float

            for (i in 0..3) {
                mResult[i] = mStart.get(i) + (mEnd.get(i) - mStart.get(i)) * value
            }

            if (mMask == null) {
                mMask = RectF()
            }

            mMask?.set(mResult.get(0), mResult.get(1), mResult.get(2), mResult.get(3))
            invalidate()
        }

        init {
            setFloatValues(0f, 1f)
            setDuration(duration)
            addUpdateListener(this)

            mStart[0] = start.left
            mStart[1] = start.top
            mStart[2] = start.right
            mStart[3] = start.bottom
            mEnd[0] = end.left
            mEnd[1] = end.top
            mEnd[2] = end.right
            mEnd[3] = end.bottom
        }
    }

    private val mLastMovePoint: PointF = PointF()
    private val mScaleCenter: PointF = PointF()
    private var mScaleBase: Float = 0f
    private var mScaleAnimator: ScaleAnimator? = null
    private var mFlingAnimator: FlingAnimator? = null
    private val mGestureDetector: GestureDetector =
        GestureDetector(this@PinchImageView.context, object : SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {

                if (pinchMode == PINCH_MODE_FREE && !(mScaleAnimator != null && mScaleAnimator?.isRunning == true)) {
                    fling(velocityX, velocityY)
                }
                return true
            }

            override fun onLongPress(e: MotionEvent) {

                if (mOnLongClickListener != null) {
                    mOnLongClickListener?.onLongClick(this@PinchImageView)
                }
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {

                if (pinchMode == PINCH_MODE_SCROLL && !(mScaleAnimator != null && mScaleAnimator?.isRunning == true)) {
                    doubleTap(e.x, e.y)
                }
                return true
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {

                if (mOnClickListener != null) {
                    mOnClickListener?.onClick(this@PinchImageView)
                }
                return true
            }
        })

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        val action: Int = event.action and MotionEvent.ACTION_MASK

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {

            if (pinchMode == PINCH_MODE_SCALE) {
                scaleEnd()
            }
            pinchMode = PINCH_MODE_FREE
        } else if (action == MotionEvent.ACTION_POINTER_UP) {

            if (pinchMode == PINCH_MODE_SCALE) {

                if (event.pointerCount > 2) {

                    if (event.action shr 8 == 0) {
                        saveScaleContext(event.getX(1), event.getY(1), event.getX(2), event.getY(2))

                    } else if (event.action shr 8 == 1) {
                        saveScaleContext(event.getX(0), event.getY(0), event.getX(2), event.getY(2))
                    }
                }

            }

        } else if (action == MotionEvent.ACTION_DOWN) {

            if (!(mScaleAnimator != null && mScaleAnimator?.isRunning == true)) {

                cancelAllAnimator()

                pinchMode = PINCH_MODE_SCROLL

                mLastMovePoint.set(event.x, event.y)
            }

        } else if (action == MotionEvent.ACTION_POINTER_DOWN) {

            cancelAllAnimator()

            pinchMode = PINCH_MODE_SCALE

            saveScaleContext(event.getX(0), event.getY(0), event.getX(1), event.getY(1))
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (!(mScaleAnimator != null && mScaleAnimator?.isRunning == true)) {

                if (pinchMode == PINCH_MODE_SCROLL) {

                    scrollBy(event.x - mLastMovePoint.x, event.y - mLastMovePoint.y)

                    mLastMovePoint.set(event.x, event.y)

                } else if (pinchMode == PINCH_MODE_SCALE && event.pointerCount > 1) {

                    val distance: Float = MathUtils.getDistance(
                        event.getX(0),
                        event.getY(0),
                        event.getX(1),
                        event.getY(1)
                    )

                    val lineCenter: FloatArray = MathUtils.getCenterPoint(
                        event.getX(0),
                        event.getY(0),
                        event.getX(1),
                        event.getY(1)
                    )
                    mLastMovePoint.set(lineCenter.get(0), lineCenter.get(1))

                    scale(mScaleCenter, mScaleBase, distance, mLastMovePoint)
                }
            }
        }

        mGestureDetector.onTouchEvent(event)
        return true
    }

    private fun scrollBy(xDiff: Float, yDiff: Float): Boolean {
        var xDiff: Float = xDiff
        var yDiff: Float = yDiff
        if (!isReady) {
            return false
        }

        val bound: RectF = MathUtils.rectFTake()!!
        getImageBound(bound)

        val displayWidth: Float = width.toFloat()
        val displayHeight: Float = height.toFloat()

        if (bound.right - bound.left < displayWidth) {
            xDiff = 0f

        } else if (bound.left + xDiff > 0) {

            xDiff = if (bound.left < 0) {
                -bound.left

            } else {
                0f
            }

        } else if (bound.right + xDiff < displayWidth) {

            xDiff = if (bound.right > displayWidth) {
                displayWidth - bound.right

            } else {
                0f
            }
        }

        if (bound.bottom - bound.top < displayHeight) {
            yDiff = 0f
        } else if (bound.top + yDiff > 0) {
            yDiff = if (bound.top < 0) {
                -bound.top
            } else {
                0f
            }
        } else if (bound.bottom + yDiff < displayHeight) {
            yDiff = if (bound.bottom > displayHeight) {
                displayHeight - bound.bottom
            } else {
                0f
            }
        }
        MathUtils.rectFGiven(bound)

        mOuterMatrix.postTranslate(xDiff, yDiff)
        dispatchOuterMatrixChanged()

        invalidate()

        return xDiff != 0f || yDiff != 0f
    }

    private fun saveScaleContext(x1: Float, y1: Float, x2: Float, y2: Float) {


        mScaleBase =
            MathUtils.getMatrixScale(mOuterMatrix).get(0) / MathUtils.getDistance(x1, y1, x2, y2)


        val center: FloatArray =
            MathUtils.inverseMatrixPoint(MathUtils.getCenterPoint(x1, y1, x2, y2), mOuterMatrix)
        mScaleCenter.set(center.get(0), center.get(1))
    }

    private fun scale(scaleCenter: PointF, scaleBase: Float, distance: Float, lineCenter: PointF) {
        if (!isReady) {
            return
        }

        val scale: Float = scaleBase * distance
        val matrix: Matrix = MathUtils.matrixTake()!!

        matrix.postScale(scale, scale, scaleCenter.x, scaleCenter.y)

        matrix.postTranslate(lineCenter.x - scaleCenter.x, lineCenter.y - scaleCenter.y)

        mOuterMatrix.set(matrix)
        MathUtils.matrixGiven(matrix)
        dispatchOuterMatrixChanged()

        invalidate()
    }

    private fun doubleTap(x: Float, y: Float) {
        if (!isReady) {
            return
        }

        val innerMatrix: Matrix? = MathUtils.matrixTake()
        getInnerMatrix(innerMatrix)

        val innerScale: Float = MathUtils.getMatrixScale(innerMatrix).get(0)
        val outerScale: Float = MathUtils.getMatrixScale(mOuterMatrix).get(0)
        val currentScale: Float = innerScale * outerScale

        val displayWidth: Float = width.toFloat()
        val displayHeight: Float = height.toFloat()

        val maxScale: Float = maxScale

        var nextScale: Float = calculateNextScale(innerScale, outerScale)

        if (nextScale > maxScale) {
            nextScale = maxScale
        }
        if (nextScale < innerScale) {
            nextScale = innerScale
        }

        val animEnd: Matrix? = MathUtils.matrixTake(mOuterMatrix)

        animEnd?.postScale(nextScale / currentScale, nextScale / currentScale, x, y)

        animEnd?.postTranslate(displayWidth / 2f - x, displayHeight / 2f - y)

        val testMatrix: Matrix? = MathUtils.matrixTake(innerMatrix)
        testMatrix?.postConcat(animEnd)
        val testBound: RectF = MathUtils.rectFTake(
            0f,
            0f,
            drawable.intrinsicWidth.toFloat(),
            drawable.intrinsicHeight.toFloat()
        )!!
        testMatrix?.mapRect(testBound)

        var postX = 0f
        var postY = 0f
        when {
            testBound.right - testBound.left < displayWidth -> postX =
                displayWidth / 2f - (testBound.right + testBound.left) / 2f
            testBound.left > 0 -> postX = -testBound.left
            testBound.right < displayWidth -> postX = displayWidth - testBound.right
        }
        if (testBound.bottom - testBound.top < displayHeight) {
            postY = displayHeight / 2f - (testBound.bottom + testBound.top) / 2f
        } else if (testBound.top > 0) {
            postY = -testBound.top
        } else if (testBound.bottom < displayHeight) {
            postY = displayHeight - testBound.bottom
        }

        animEnd?.postTranslate(postX, postY)

        cancelAllAnimator()

        mScaleAnimator = animEnd?.let { ScaleAnimator(mOuterMatrix, it) }
        mScaleAnimator?.start()

        MathUtils.rectFGiven(testBound)
        testMatrix?.let { MathUtils.matrixGiven(it) }
        animEnd?.let { MathUtils.matrixGiven(it) }
        innerMatrix?.let { MathUtils.matrixGiven(it) }
    }

    private fun scaleEnd() {
        if (!isReady) {
            return
        }

        var change: Boolean = false

        val currentMatrix: Matrix? = MathUtils.matrixTake()
        currentMatrix?.let { getCurrentImageMatrix(it) }

        val currentScale: Float = MathUtils.getMatrixScale(currentMatrix).get(0)

        val outerScale: Float = MathUtils.getMatrixScale(mOuterMatrix).get(0)

        val displayWidth: Float = width.toFloat()
        val displayHeight: Float = height.toFloat()

        val maxScale: Float = maxScale

        var scalePost = 1f

        var postX = 0f
        var postY = 0f

        if (currentScale > maxScale) {
            scalePost = maxScale / currentScale
        }

        if (outerScale * scalePost < 1f) {
            scalePost = 1f / outerScale
        }

        if (scalePost != 1f) {
            change = true
        }

        val testMatrix: Matrix? = MathUtils.matrixTake(currentMatrix)
        testMatrix?.postScale(scalePost, scalePost, mLastMovePoint.x, mLastMovePoint.y)
        val testBound: RectF = MathUtils.rectFTake(
            0f,
            0f,
            drawable.intrinsicWidth.toFloat(),
            drawable.intrinsicHeight.toFloat()
        )!!

        testMatrix?.mapRect(testBound)

        when {
            testBound.right - testBound.left < displayWidth -> postX =
                displayWidth / 2f - (testBound.right + testBound.left) / 2f
            testBound.left > 0 -> postX = -testBound.left
            testBound.right < displayWidth -> postX = displayWidth - testBound.right
        }
        when {
            testBound.bottom - testBound.top < displayHeight -> postY =
                displayHeight / 2f - (testBound.bottom + testBound.top) / 2f
            testBound.top > 0 -> postY = -testBound.top
            testBound.bottom < displayHeight -> postY = displayHeight - testBound.bottom
        }

        if (postX != 0f || postY != 0f) {
            change = true
        }

        if (change) {

            val animEnd: Matrix? = MathUtils.matrixTake(mOuterMatrix)
            animEnd?.postScale(scalePost, scalePost, mLastMovePoint.x, mLastMovePoint.y)
            animEnd?.postTranslate(postX, postY)

            cancelAllAnimator()

            mScaleAnimator = animEnd?.let { ScaleAnimator(mOuterMatrix, it) }
            mScaleAnimator?.start()

            animEnd?.let { MathUtils.matrixGiven(it) }
        }

        MathUtils.rectFGiven(testBound)
        testMatrix?.let { MathUtils.matrixGiven(it) }
        currentMatrix?.let { MathUtils.matrixGiven(it) }
    }

    private fun fling(vx: Float, vy: Float) {
        if (!isReady) {
            return
        }

        cancelAllAnimator()


        mFlingAnimator = FlingAnimator(vx / 60f, vy / 60f)
        mFlingAnimator?.start()
    }

    private fun cancelAllAnimator() {
        if (mScaleAnimator != null) {
            mScaleAnimator?.cancel()
            mScaleAnimator = null
        }
        if (mFlingAnimator != null) {
            mFlingAnimator?.cancel()
            mFlingAnimator = null
        }
    }

    private inner class FlingAnimator(vectorX: Float, vectorY: Float) :
        ValueAnimator(), AnimatorUpdateListener {
        private val mVector: FloatArray
        override fun onAnimationUpdate(animation: ValueAnimator) {

            val result: Boolean = scrollBy(mVector.get(0), mVector.get(1))

            mVector[0] *= FLING_DAMPING_FACTOR
            mVector[1] *= FLING_DAMPING_FACTOR

            if (!result || MathUtils.getDistance(0f, 0f, mVector.get(0), mVector.get(1)) < 1f) {
                animation.cancel()
            }
        }

        init {
            setFloatValues(0f, 1f)
            duration = 1000000
            addUpdateListener(this)
            mVector = floatArrayOf(vectorX, vectorY)
        }
    }

    private inner class ScaleAnimator @JvmOverloads constructor(
        start: Matrix,
        end: Matrix,
        duration: Long = SCALE_ANIMATOR_DURATION.toLong()
    ) :
        ValueAnimator(), AnimatorUpdateListener {
        private val mStart: FloatArray = FloatArray(9)
        private val mEnd: FloatArray = FloatArray(9)
        private val mResult: FloatArray = FloatArray(9)

        override fun onAnimationUpdate(animation: ValueAnimator) {
            val value: Float = animation.animatedValue as Float
            for (i in 0..8) {
                (mStart[i] + (mEnd[i] - mStart[i]) * value).also { mResult[i] = it }
            }

            mOuterMatrix.setValues(mResult)
            dispatchOuterMatrixChanged()
            invalidate()
        }

        init {
            setFloatValues(0f, 1f)
            setDuration(duration)
            addUpdateListener(this)
            start.getValues(mStart)
            end.getValues(mEnd)
        }
    }

    private abstract class ObjectsPool<T>(
        private val mSize: Int
    ) {
        private val mQueue: Queue<T>
        fun take(): T {

            return if (mQueue.size == 0) {
                newInstance()
            } else {

                resetInstance(mQueue.poll())
            }
        }

        fun given(obj: T?) {

            if (obj != null && mQueue.size < mSize) {
                mQueue.offer(obj)
            }
        }

        protected abstract fun newInstance(): T
        protected abstract fun resetInstance(obj: T): T

        init {
            mQueue = LinkedList()
        }
    }

    private class MatrixPool(size: Int) : ObjectsPool<Matrix?>(size) {
        override fun newInstance(): Matrix {
            return Matrix()
        }

        override fun resetInstance(obj: Matrix?): Matrix? {
            obj?.reset()
            return obj
        }
    }

    private class RectFPool(size: Int) : ObjectsPool<RectF?>(size) {
        override fun newInstance(): RectF {
            return RectF()
        }

        override fun resetInstance(obj: RectF?): RectF? {
            obj?.setEmpty()
            return obj
        }
    }

    object MathUtils {
        private val mMatrixPool: MatrixPool = MatrixPool(16)

        fun matrixTake(): Matrix? {
            return (mMatrixPool.take())
        }

        fun matrixTake(matrix: Matrix?): Matrix? {
            val result: Matrix? = (mMatrixPool.take())
            if (matrix != null) {
                result?.set(matrix)
            }
            return result
        }

        fun matrixGiven(matrix: Matrix) {
            mMatrixPool.given(matrix)
        }

        private val mRectFPool: RectFPool = RectFPool(16)

        fun rectFTake(): RectF? {
            return (mRectFPool.take())
        }

        fun rectFTake(left: Float, top: Float, right: Float, bottom: Float): RectF? {
            val result: RectF? = (mRectFPool.take())
            result?.set(left, top, right, bottom)
            return result
        }

        fun rectFTake(rectF: RectF?): RectF? {
            val result: RectF? = (mRectFPool.take())
            if (rectF != null) {
                result?.set(rectF)
            }
            return result
        }

        fun rectFGiven(rectF: RectF) {
            mRectFPool.given(rectF)
        }

        fun getDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
            val x: Float = x1 - x2
            val y: Float = y1 - y2
            return Math.sqrt((x * x + y * y).toDouble()).toFloat()
        }

        fun getCenterPoint(x1: Float, y1: Float, x2: Float, y2: Float): FloatArray {
            return floatArrayOf((x1 + x2) / 2f, (y1 + y2) / 2f)
        }

        fun getMatrixScale(matrix: Matrix?): FloatArray {
            return if (matrix != null) {
                val value: FloatArray = FloatArray(9)
                matrix.getValues(value)
                floatArrayOf(value.get(0), value.get(4))
            } else {
                FloatArray(2)
            }
        }

        fun inverseMatrixPoint(point: FloatArray?, matrix: Matrix?): FloatArray {
            return if (point != null && matrix != null) {
                val dst = FloatArray(2)

                val inverse: Matrix? = matrixTake()
                matrix.invert(inverse)

                inverse?.mapPoints(dst, point)

                inverse?.let { matrixGiven(it) }
                dst
            } else {
                FloatArray(2)
            }
        }

        fun calculateRectTranslateMatrix(from: RectF?, to: RectF?, result: Matrix?) {
            if ((from == null) || (to == null) || (result == null)) {
                return
            }
            if (from.width() == 0f || from.height() == 0f) {
                return
            }
            result.reset()
            result.postTranslate(-from.left, -from.top)
            result.postScale(to.width() / from.width(), to.height() / from.height())
            result.postTranslate(to.left, to.top)
        }

        fun calculateScaledRectInContainer(
            container: RectF?,
            srcWidth: Float,
            srcHeight: Float,
            scaleType: ScaleType?,
            result: RectF?
        ) {
            var scaleType: ScaleType? = scaleType
            if (container == null || result == null) {
                return
            }
            if (srcWidth == 0f || srcHeight == 0f) {
                return
            }

            if (scaleType == null) {
                scaleType = ScaleType.FIT_CENTER
            }
            result.setEmpty()
            if ((ScaleType.FIT_XY == scaleType)) {
                result.set(container)
            } else if ((ScaleType.CENTER == scaleType)) {
                val matrix: Matrix? = matrixTake()
                val rect: RectF? = rectFTake(0f, 0f, srcWidth, srcHeight)
                matrix?.setTranslate(
                    (container.width() - srcWidth) * 0.5f,
                    (container.height() - srcHeight) * 0.5f
                )
                matrix?.mapRect(result, rect)
                rect?.let { rectFGiven(it) }
                matrix?.let { matrixGiven(it) }
                result.left += container.left
                result.right += container.left
                result.top += container.top
                result.bottom += container.top
            } else if ((ScaleType.CENTER_CROP == scaleType)) {
                val matrix: Matrix? = matrixTake()
                val rect: RectF? = rectFTake(0f, 0f, srcWidth, srcHeight)
                val scale: Float
                var dx = 0f
                var dy = 0f
                if (srcWidth * container.height() > container.width() * srcHeight) {
                    scale = container.height() / srcHeight
                    dx = (container.width() - srcWidth * scale) * 0.5f
                } else {
                    scale = container.width() / srcWidth
                    dy = (container.height() - srcHeight * scale) * 0.5f
                }
                matrix?.setScale(scale, scale)
                matrix?.postTranslate(dx, dy)
                matrix?.mapRect(result, rect)
                rect?.let { rectFGiven(it) }
                matrix?.let { matrixGiven(it) }
                result.left += container.left
                result.right += container.left
                result.top += container.top
                result.bottom += container.top
            } else if ((ScaleType.CENTER_INSIDE == scaleType)) {
                val matrix: Matrix? = matrixTake()
                val rect: RectF? = rectFTake(0f, 0f, srcWidth, srcHeight)
                val dy: Float
                val scale: Float =
                    if (srcWidth <= container.width() && srcHeight <= container.height()) {
                        1f
                    } else {
                        (container.width() / srcWidth).coerceAtMost(container.height() / srcHeight)
                    }
                val dx: Float = (container.width() - srcWidth * scale) * 0.5f
                dy = (container.height() - srcHeight * scale) * 0.5f
                matrix?.setScale(scale, scale)
                matrix?.postTranslate(dx, dy)
                matrix?.mapRect(result, rect)
                rect?.let { rectFGiven(it) }
                matrix?.let { matrixGiven(it) }
                result.left += container.left
                result.right += container.left
                result.top += container.top
                result.bottom += container.top
            } else if ((ScaleType.FIT_CENTER == scaleType)) {
                val matrix: Matrix? = matrixTake()
                val rect: RectF? = rectFTake(0f, 0f, srcWidth, srcHeight)
                val tempSrc: RectF? = rectFTake(0f, 0f, srcWidth, srcHeight)
                val tempDst: RectF? = rectFTake(0f, 0f, container.width(), container.height())
                matrix?.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.CENTER)
                matrix?.mapRect(result, rect)
                tempDst?.let { rectFGiven(it) }
                tempSrc?.let { rectFGiven(it) }
                rect?.let { rectFGiven(it) }
                matrix?.let { matrixGiven(it) }
                result.left += container.left
                result.right += container.left
                result.top += container.top
                result.bottom += container.top
            } else if ((ScaleType.FIT_START == scaleType)) {
                val matrix: Matrix? = matrixTake()
                val rect: RectF? = rectFTake(0f, 0f, srcWidth, srcHeight)
                val tempSrc: RectF? = rectFTake(0f, 0f, srcWidth, srcHeight)
                val tempDst: RectF? = rectFTake(0f, 0f, container.width(), container.height())
                matrix?.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.START)
                matrix?.mapRect(result, rect)
                tempDst?.let { rectFGiven(it) }
                tempSrc?.let { rectFGiven(it) }
                rect?.let { rectFGiven(it) }
                matrix?.let { matrixGiven(it) }
                result.left += container.left
                result.right += container.left
                result.top += container.top
                result.bottom += container.top
            } else if ((ScaleType.FIT_END == scaleType)) {
                val matrix: Matrix? = matrixTake()
                val rect: RectF? = rectFTake(0f, 0f, srcWidth, srcHeight)
                val tempSrc: RectF? = rectFTake(0f, 0f, srcWidth, srcHeight)
                val tempDst: RectF? = rectFTake(0f, 0f, container.width(), container.height())
                matrix?.setRectToRect(tempSrc, tempDst, Matrix.ScaleToFit.END)
                matrix?.mapRect(result, rect)
                tempDst?.let { rectFGiven(it) }
                tempSrc?.let { rectFGiven(it) }
                rect?.let { rectFGiven(it) }
                matrix?.let { matrixGiven(it) }
                result.left += container.left
                result.right += container.left
                result.top += container.top
                result.bottom += container.top
            } else {
                result.set(container)
            }
        }
    }

    companion object {
        const val maxScale: Float = 4f
        const val SCALE_ANIMATOR_DURATION: Int = 200
        const val FLING_DAMPING_FACTOR: Float = 0.9f
        const val PINCH_MODE_FREE: Int = 0
        const val PINCH_MODE_SCROLL: Int = 1
        const val PINCH_MODE_SCALE: Int = 2
    }
}