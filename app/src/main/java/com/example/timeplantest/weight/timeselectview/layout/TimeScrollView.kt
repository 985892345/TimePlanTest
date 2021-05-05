package com.example.timeplantest.weight.timeselectview.layout

import android.annotation.SuppressLint
import android.content.Context
import androidx.viewpager2.widget.ViewPager2
import com.example.timeplantest.weight.timeselectview.utils.TSViewInternalData
import com.example.timeplantest.weight.timeselectview.utils.TSViewLongClick
import com.example.timeplantest.weight.timeselectview.utils.TSViewLongClick.*
import com.example.timeplantest.weight.timeselectview.utils.TSViewTimeUtil
import com.example.timeplantest.weight.timeselectview.utils.tscrollview.TScrollViewTouchEvent
import com.example.timeplantest.weight.timeselectview.viewinterface.IRectManger
import com.example.timeplantest.weight.timeselectview.viewinterface.ITSViewTimeUtil
import com.example.timeplantest.weight.timeselectview.viewinterface.ITimeScrollView
import kotlin.math.min

/**
 * @author 985892345
 * @date 2021/3/20
 * @description [com.example.timeplantest.weight.timeselectview.TimeSelectView]之下，[ScrollLayout]之上
 */
@SuppressLint("ViewConstructor")
class TimeScrollView(context: Context, iTimeScrollView: ITimeScrollView, data: TSViewInternalData, time: ITSViewTimeUtil, rectManger: IRectManger) : TScrollViewTouchEvent(context) {

    /**
     * 解决与ViewPager2的同向滑动冲突
     * @param viewPager2 传入ViewPager2，不是ViewPager
     */
    fun setLinkedViewPager2(viewPager2: ViewPager2, onVpInterceptionStart: ((linkedViewPager2: ViewPager2, rawY: Float) -> Unit)? = null) {
        mLinkedViewPager2 = viewPager2
        super.setLinkViewPager2(viewPager2, onVpInterceptionStart)
    }

    /**
     * 设置能否长按，如果设置为了false，手指又触摸了屏幕，则会停止长按的判断，此时如果仍符合长按的条件，可以设置true后重启长按
     */
    fun setIsCanLongClick(boolean: Boolean) {
        mIsCanLongClick = boolean
        if (boolean && mIsCloseLongClickJudge && mClickRectViewPosition != null) {
            mIsCloseLongClickJudge = false
            restartLongClickJudge()
        }
    }

    /**
     * 回到CurrentTime
     */
    fun backCurrentTime() {
        removeCallbacks(mAfterUpBackCurrentTimeRun)
        post(mAfterUpBackCurrentTimeRun)
    }

    /**
     * 快速地回到CurrentTime
     */
    fun fastBackCurrentTime() {
        scrollY = when (mData.mCenterTime) {
            TSViewTimeUtil.CENTER_TIME_NOW_TIME -> { //以当前时间线为中线
                mTime.getNowTimeHeight() - height / 2
            }
            TSViewTimeUtil.CENTER_TIME_CENTER -> { //以中心值为中线
                mData.mInsideTotalHeight / 2 - height / 2
            }
            else -> { //以你设定的时间为中线
                mTime.getTimeHeight(mData.mCenterTime) - height / 2
            }
        }
    }

    /**
     * 用于取消回到CurrentTime
     */
    fun cancelAfterUpBackCurrentTimeRun() {
        removeCallbacks(mAfterUpBackCurrentTimeRun)
    }

    /**
     * 用于整体移动、矩形边界改变、绘制新的矩形，上述情况在松手时自动滑动到适宜的高度
     * @param height 默认为-1，代表整体移动的矩形能在新位置放下，那就自动滑到合适的高度；
     * 当传入值时，表示整体移动的矩形不能在新的位置放下，那就滑到原来的高度，此高度由调用者提供
     */
    fun scrollToSuitableHeight(height: Int = -1) {
        if (height == -1) {
            var dy = 0
            //以下为自动滑到适当位置的判断
            when (mData.mCondition) {
                TOP_SLIDE_DOWN, BOTTOM_SLIDE_DOWN, EMPTY_SLIDE_DOWN -> { //时间轴向上滑
                    dy = mOuterUpY - (this.height - AUTO_MOVE_THRESHOLD) + 10
                }
                TOP_SLIDE_UP, BOTTOM_SLIDE_UP, EMPTY_SLIDE_UP -> { //时间轴向下滑
                    dy = mOuterUpY - AUTO_MOVE_THRESHOLD - 10
                }
                INSIDE_SLIDE_DOWN -> { //时间轴向上滑
                    val bottom = mITimeScrollView.getOuterBottom()
                    dy = bottom - (this.height - AUTO_MOVE_THRESHOLD) + 10
                }
                INSIDE_SLIDE_UP -> { //时间轴向下滑
                    val top = mITimeScrollView.getOuterTop()
                    dy = top - AUTO_MOVE_THRESHOLD - 10
                }
                else -> {
                }
            }
            slowlyScrollBy(dy)
        }else {
            slowlyScrollTo(height - this.height / 2)
        }
    }

    companion object {
        private const val AUTO_MOVE_THRESHOLD = 150 //自动滑动的阈值
        private const val MAX_AUTO_SLIDE_VELOCITY = 7F //最大滑动速度
        private const val MULTIPLE = MAX_AUTO_SLIDE_VELOCITY / AUTO_MOVE_THRESHOLD
    }

    private val mData = data
    private val mTime = time
    private val mRectManger = rectManger
    private val mITimeScrollView = iTimeScrollView
    private var mLinkedViewPager2: ViewPager2? = null

    private var mIsCanLongClick = true
    var mClickRectViewPosition: Int? = null

    init {
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, data.mInsideTotalHeight)
        iTimeScrollView.addScrollLayout(lp, this)
        data.setOnConditionEndListener {
            onLongClickEnd(it)
        }
        scrollToCenterTime()
        isVerticalScrollBarEnabled = false //取消滚动条
        overScrollMode = OVER_SCROLL_NEVER //取消滑到边界的上下虚影
    }

    private fun scrollToCenterTime() {
        post {
            scrollY = when (mData.mCenterTime) { //以当前时间线为中线
                TSViewTimeUtil.CENTER_TIME_NOW_TIME -> {
                    postDelayed(mBackNowTimeRun, TSViewTimeUtil.DELAY_NOW_TIME_REFRESH)
                    mTime.getNowTimeHeight() - height / 2
                }
                TSViewTimeUtil.CENTER_TIME_CENTER -> { //以中心值为中线
                    mData.mInsideTotalHeight / 2 - height / 2
                }
                else -> { //以你设定的时间为中线，不随时间移动
                    mTime.getTimeHeight(mData.mCenterTime) - height / 2
                }
            }
        }
    }

    private val mBackNowTimeRun = object : Runnable {
        override fun run() {
            slowlyScrollTo(mTime.getNowTimeHeight() - height / 2)
            postDelayed(this, TSViewTimeUtil.DELAY_NOW_TIME_REFRESH)
        }
    }

    private val mAfterUpBackCurrentTimeRun = object : Runnable {
        override fun run() {
            slowlyScrollTo(when (mData.mCenterTime) {
                TSViewTimeUtil.CENTER_TIME_NOW_TIME -> {
                    postDelayed(this, TSViewTimeUtil.DELAY_NOW_TIME_REFRESH)
                    mTime.getNowTimeHeight() - height / 2
                }
                TSViewTimeUtil.CENTER_TIME_CENTER -> {
                    mData.mInsideTotalHeight / 2 - height / 2
                }
                else -> {
                    mTime.getTimeHeight(mData.mCenterTime) - height / 2
                }
            })
        }
    }

    override fun dispatchTouchEventDown(outerX: Int, outerY: Int) {
        removeCallbacks(mAfterUpBackCurrentTimeRun)
        removeCallbacks(mBackNowTimeRun)
    }

    private var mOuterUpY = 0
    override fun dispatchTouchEventUp(outerX: Int, outerY: Int) {
        mOuterUpY = outerY
        postDelayed(mAfterUpBackCurrentTimeRun, TSViewTimeUtil.DELAY_BACK_CURRENT_TIME)
        if (mStartRun) {
            mStartRun = false
            removeCallbacks(mSlideRunnable)
        }
    }

    private var mInitialX = 0
    private var mInitialY = 0
    private var mInitialScrollY = 0
    private var mIsCloseLongClickJudge = false
    override fun onInterceptTouchEventDown(outerX: Int, outerY: Int, onScreenX: Int, onScreenY: Int): Boolean {
        mInitialX = outerX
        mInitialY = outerY
        mInitialScrollY = scrollY
        if (!mIsCanLongClick) { //用于在动画还在运行时，禁止长按
            mIsCloseLongClickJudge = true
            closeLongClickJudge()
        }
        mClickRectViewPosition = mITimeScrollView.getRectViewPosition(onScreenX)
        return if (mClickRectViewPosition == null) {
            true
        }else {
            //只对RectView的位置内的外部大小的RectView实际绘制区域不拦截
            outerY !in mData.mRectViewTop..mData.mRectViewBottom
        }
    }

    override fun isInLongClickArea(outerX: Int, outerY: Int, onScreenX: Int, onScreenY: Int): Boolean {
        return mClickRectViewPosition != null
    }

    override fun onLongClickStartButNotMove(): Boolean {
        mITimeScrollView.onLongClickStartButNotMove(mClickRectViewPosition!!)
        return true
    }

    override fun onClick(insideX: Int, insideY: Int): Boolean {
        if (mClickRectViewPosition != null) {
            val bean = mRectManger.getBean(insideY, mClickRectViewPosition!!)
            if (bean != null) {
                mData.mOnClickListener?.invoke(bean)
            }
        }
        return true
    }

    override fun onLongClickStart(insideX: Int, insideY: Int, onScreenX: Int, onScreenY: Int) {
        mData.mIsLongClick = true
        mRectManger.longClickConditionJudge(insideY, mClickRectViewPosition!!) //对于刷新所有的RectView我放在了ScrollLayout中
        mUpperLimit = mRectManger.getClickUpperLimit()
        mLowerLimit = mRectManger.getClickLowerLimit()
        mForbidSlideCenter = insideY - scrollY
        mData.mOnLongClickStartListener?.invoke(mData.mCondition)
    }

    private fun onLongClickEnd(condition: TSViewLongClick) {
        mData.mIsLongClick = false
        mData.mOnLongClickEndListener?.invoke(condition)
    }

    private var mOuterX = 0
    private var mOuterY = 0
    private var mPreOuterY = 0
    private var mVelocity = 0
    private var mStartRun = false
    private var mForbidSlideCenter = 0
    private var mUpperLimit = Int.MIN_VALUE
    private var mLowerLimit = Int.MAX_VALUE
    private val mSlideRunnable = object: Runnable {
        override fun run() {
            scrollBy(0, mVelocity)
            when (mData.mCondition) {
                TOP_SLIDE_UP, TOP_SLIDE_DOWN,
                BOTTOM_SLIDE_UP, BOTTOM_SLIDE_DOWN,
                EMPTY_SLIDE_UP, EMPTY_SLIDE_DOWN -> {
                    //要是不手动调用，在手指不移动却又在自动滑动的情况下RctView不会自动更新
                    iTimeScrollView.slideDrawRect(scrollY + mPreOuterY, mClickRectViewPosition!!)
                }
                INSIDE_SLIDE_UP, INSIDE_SLIDE_DOWN -> {
                    iTimeScrollView.slideRectImgView(mOuterX - mInitialX, mOuterY - mInitialY + scrollY - mInitialScrollY)
                }
                else -> {}
            }
            mPreOuterY = mOuterY
            postDelayed(this, 15)
        }
    }
    override fun automaticSlide(outerX: Int, outerY: Int, insideX: Int, insideY: Int) {
        mOuterX = outerX
        mOuterY = outerY
        when (mData.mCondition) {
            TOP, TOP_SLIDE_UP, TOP_SLIDE_DOWN,
            BOTTOM, BOTTOM_SLIDE_UP, BOTTOM_SLIDE_DOWN,
            EMPTY_AREA, EMPTY_SLIDE_UP, EMPTY_SLIDE_DOWN -> {
                val isTopSlide = outerY < AUTO_MOVE_THRESHOLD
                val isBottomSlide = outerY > height - AUTO_MOVE_THRESHOLD
                val isInCanSlideLimit = insideY in mUpperLimit..mLowerLimit
                //50为一个上下的阈值，只有移动超过了这个阈值，才可以自动滑动
                val isInForbidSlideLimit = outerY in (mForbidSlideCenter - 10)..(mForbidSlideCenter + 10)
                if (!isTopSlide && !isBottomSlide || !isInCanSlideLimit || isInForbidSlideLimit ||
                        scrollY == 0 && isTopSlide || scrollY + height == mData.mInsideTotalHeight && isBottomSlide) {
                    removeCallbacks(mSlideRunnable)
                    mStartRun = false
                    mData.mStartAutoSlide = false
                    when (mData.mCondition) {
                        TOP_SLIDE_UP, TOP_SLIDE_DOWN -> {
                            mData.mCondition = TOP
                        }
                        BOTTOM_SLIDE_UP, BOTTOM_SLIDE_DOWN -> {
                            mData.mCondition = BOTTOM
                        }
                        EMPTY_SLIDE_UP, EMPTY_SLIDE_DOWN -> {
                            mData.mCondition = EMPTY_AREA
                        }
                        else -> {
                        }
                    }
                }else {
                    if (isTopSlide) { //时间轴往下滑
                        if (outerY > mPreOuterY + 5) { //时间轴往下滑中，突然你也向下滑，此时禁止自动滑动
                            mPreOuterY = outerY
                            mForbidSlideCenter = insideY - scrollY
                            return
                        }
                        mVelocity = -min((AUTO_MOVE_THRESHOLD - outerY) * MULTIPLE, MAX_AUTO_SLIDE_VELOCITY).toInt()
                        when (mData.mCondition) {
                            TOP -> {
                                mData.mCondition = TOP_SLIDE_UP
                            }
                            BOTTOM -> {
                                mData.mCondition = BOTTOM_SLIDE_UP
                            }
                            EMPTY_AREA -> {
                                mData.mCondition = EMPTY_SLIDE_UP
                            }
                            else -> {
                            }
                        }
                    }else { //时间轴往上滑
                        if (outerY < mPreOuterY - 5) { //时间轴往上滑中，突然你也向上滑，此时禁止自动滑动
                            mPreOuterY = outerY
                            mForbidSlideCenter = insideY - scrollY
                            return
                        }
                        mVelocity = min((outerY - (height - AUTO_MOVE_THRESHOLD)) * MULTIPLE, MAX_AUTO_SLIDE_VELOCITY).toInt()
                        when (mData.mCondition) {
                            TOP -> {
                                mData.mCondition = TOP_SLIDE_DOWN
                            }
                            BOTTOM -> {
                                mData.mCondition = BOTTOM_SLIDE_DOWN
                            }
                            EMPTY_AREA -> {
                                mData.mCondition = EMPTY_SLIDE_DOWN
                            }
                            else -> {
                            }
                        }
                    }
                    if (!mStartRun) {
                        mStartRun = true
                        mData.mStartAutoSlide = true
                        mPreOuterY = outerY
                        post(mSlideRunnable)
                    }
                }
            }
            INSIDE, INSIDE_SLIDE_UP, INSIDE_SLIDE_DOWN -> {
                val top = mITimeScrollView.getOuterTop()
                val bottom = mITimeScrollView.getOuterBottom()
                val isTopSlide = top < AUTO_MOVE_THRESHOLD * 0.4F
                val isBottomSlide = bottom > height - AUTO_MOVE_THRESHOLD * 0.4F
                //20为一个上下的阈值，只有移动超过了这个阈值，才可以自动滑动
                val isInForbidSlideLimit = outerY in (mForbidSlideCenter - 20)..(mForbidSlideCenter + 20)
                if (!isTopSlide && !isBottomSlide || isInForbidSlideLimit ||
                        scrollY == 0 && isTopSlide || scrollY + height == mData.mInsideTotalHeight && isBottomSlide) {
                    removeCallbacks(mSlideRunnable)
                    mStartRun = false
                    mData.mStartAutoSlide = false
                    mData.mCondition = INSIDE
                }else {
                    if (isTopSlide) { //时间轴往下滑
                        if (outerY > mPreOuterY + 5) { //时间轴往下滑中，突然你也向下滑，此时禁止自动滑动
                            mPreOuterY = outerY
                            mForbidSlideCenter = insideY - scrollY
                            return
                        }
                        mVelocity = -min((AUTO_MOVE_THRESHOLD - top) * MULTIPLE, MAX_AUTO_SLIDE_VELOCITY).toInt()
                        mData.mCondition = INSIDE_SLIDE_UP
                    }else { //时间轴往上滑
                        if (outerY < mPreOuterY - 5) { //时间轴往上滑中，突然你也向上滑，此时禁止自动滑动
                            mPreOuterY = outerY
                            mForbidSlideCenter = insideY - scrollY
                            return
                        }
                        mVelocity = min((bottom - (height - AUTO_MOVE_THRESHOLD)) * MULTIPLE, MAX_AUTO_SLIDE_VELOCITY).toInt()
                        mData.mCondition = INSIDE_SLIDE_DOWN
                    }
                    if (!mStartRun) {
                        mStartRun = true
                        mData.mStartAutoSlide = true
                        mPreOuterY = outerY
                        mForbidSlideCenter = height / 2
                        post(mSlideRunnable)
                    }
                }
            }
            else -> {}
        }
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        mLinkedViewPager2?.let {
            val currentItem = it.currentItem
            if (currentItem == mITimeScrollView.getVpPosition()) {
                mITimeScrollView.onScrollChanged(t)
                mData.mOnScrollListener?.invoke(t, currentItem)
            }
        }
    }
}
