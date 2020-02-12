package com.bluecrunch.bluecrunchverification

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.verification_view.view.*

open class VerificationView : ConstraintLayout {

    private var mContext: Context? = null

    var boxCount = 4
    var boxBG = 0
    var boxBGFocused = 0
    var boxHeight = 0f
    var boxTextColor = 0
    var boxSpace = 0
    var verificationCodeText = ""
    /**
     * @param context:context for your activity
     */
    constructor(context: Context?) : super(context) {
        mContext = context
        inflate()
        bindViews()
        setNumberOfDigits()
        listenForTextChanges()
    }

    /**
     * @param context:context for your activity
     * @param attrs:attributeSet for your custom style
     */
    @SuppressLint("Recycle")
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        mContext = context
        initStyleables(attrs)
        inflate()
        bindViews()
        setNumberOfDigits()
        listenForTextChanges()
    }


    /**
     * @param attrs:attributeSet for your custom style
     * @return
     */
    private fun initStyleables(attrs: AttributeSet?) {
        val typeArr = context?.obtainStyledAttributes(
            attrs
            , R.styleable.VerificationView, 0, 0
        )
        boxCount = typeArr!!.getInt(R.styleable.VerificationView_box_count, 4)
        boxBG = typeArr.getResourceId(
            R.styleable.VerificationView_box_background,
            R.drawable.verification_box_default_bg
        )
        boxHeight = typeArr.getDimension(R.styleable.VerificationView_box_height, 50f)
        boxTextColor = typeArr.getColor(
            R.styleable.VerificationView_box_text_color,
            mContext?.getColor(android.R.color.black)!!
        )
        boxSpace = typeArr.getDimension(R.styleable.VerificationView_box_space, 10f)
            .toInt()

        boxBGFocused = typeArr.getResourceId(
            R.styleable.VerificationView_box_background_focused,
            boxBG
        )
        typeArr.recycle()
    }

    /**
     * This method is user to bing the views,
     */
    private fun bindViews() {
        /** set custom drawable to code layout **/
        code_1_layout.background = mContext?.getDrawable(boxBG)
        code_2_layout.background = mContext?.getDrawable(boxBG)
        code_3_layout.background = mContext?.getDrawable(boxBG)
        code_4_layout.background = mContext?.getDrawable(boxBG)
        code_5_layout.background = mContext?.getDrawable(boxBG)
        code_6_layout.background = mContext?.getDrawable(boxBG)

        /** set custom height to code layout **/
        code_1_layout.layoutParams.height = boxHeight.toInt()
        code_1_layout.requestLayout()
        code_2_layout.layoutParams.height = boxHeight.toInt()
        code_2_layout.requestLayout()
        code_3_layout.layoutParams.height = boxHeight.toInt()
        code_3_layout.requestLayout()
        code_4_layout.layoutParams.height = boxHeight.toInt()
        code_4_layout.requestLayout()
        code_5_layout.layoutParams.height = boxHeight.toInt()
        code_5_layout.requestLayout()
        code_6_layout.layoutParams.height = boxHeight.toInt()
        code_6_layout.requestLayout()

        /** set custom color to code text  **/
        verification_1_textView.setTextColor(boxTextColor)
        verification_2_textView.setTextColor(boxTextColor)
        verification_3_textView.setTextColor(boxTextColor)
        verification_4_textView.setTextColor(boxTextColor)
        verification_5_textView.setTextColor(boxTextColor)
        verification_6_textView.setTextColor(boxTextColor)

        /** set custom space between each box in code layout **/
        (code_1_layout.layoutParams as?
                MarginLayoutParams)?.setMargins(boxSpace, 0, 0, 0)
        (code_2_layout.layoutParams as?
                MarginLayoutParams)?.setMargins(boxSpace, 0, 0, 0)
        (code_3_layout.layoutParams as?
                MarginLayoutParams)?.setMargins(boxSpace, 0, 0, 0)
        (code_4_layout.layoutParams as?
                MarginLayoutParams)?.setMargins(boxSpace, 0, 0, 0)
        (code_5_layout.layoutParams as?
                MarginLayoutParams)?.setMargins(boxSpace, 0, 0, 0)
        (code_6_layout.layoutParams as?
                MarginLayoutParams)?.setMargins(boxSpace, 0, 0, 0)
    }

    /**
     * This method is used to init number of digits for code layout
     */
    private fun setNumberOfDigits() {
        val fArray = arrayOfNulls<InputFilter>(1)
        fArray[0] = InputFilter.LengthFilter(boxCount)
        typed_editText.filters = fArray
        when (boxCount) {
            4 -> {
                code_5_layout.visibility = GONE
                code_6_layout.visibility = GONE
            }
            5 -> code_6_layout.visibility = GONE
        }
    }

    /**
     * This method is used to inflate verification layout
     */
    private fun inflate() {
        val layoutInflater = LayoutInflater.from(context)
        layoutInflater.inflate(
            R.layout.verification_view,
            this, true
        )
    }

    /**
     * This method is used to listen for text changes in code editText
     */
    private fun listenForTextChanges() {
        typed_editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                verificationCodeText = charSequence.toString()
                while (verificationCodeText.length < boxCount) {
                    verificationCodeText += " "
                }
                setDigitsText()
                enableFocusedOption()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }


    /**
     * This method is used to automatic set digits in every box
     */
    private fun setDigitsText() {
        when (boxCount) {
            4 -> {
                verification_1_textView.text = verificationCodeText[0].toString()
                verification_2_textView.text = verificationCodeText[1].toString()
                verification_3_textView.text = verificationCodeText[2].toString()
                verification_4_textView.text = verificationCodeText[3].toString()
            }
            5 -> {
                verification_1_textView.text = verificationCodeText[0].toString()
                verification_2_textView.text = verificationCodeText[1].toString()
                verification_3_textView.text = verificationCodeText[2].toString()
                verification_4_textView.text = verificationCodeText[3].toString()
                verification_5_textView.text = verificationCodeText[4].toString()
            }
            6 -> {
                verification_1_textView.text = verificationCodeText[0].toString()
                verification_2_textView.text = verificationCodeText[1].toString()
                verification_3_textView.text = verificationCodeText[2].toString()
                verification_4_textView.text = verificationCodeText[3].toString()
                verification_5_textView.text = verificationCodeText[4].toString()
                verification_6_textView.text = verificationCodeText[5].toString()
            }
        }
    }

    /**
     * This method is used to empty digits in every box
     */
    fun invalidateDigits() {
        when (boxCount) {
            4 -> {
                verification_1_textView.text = ""
                verification_2_textView.text = ""
                verification_3_textView.text = ""
                verification_4_textView.text = ""
                setDrawableFocusedFor4Digits()
            }
            5 -> {
                verification_1_textView.text = ""
                verification_2_textView.text = ""
                verification_3_textView.text = ""
                verification_4_textView.text = ""
                verification_5_textView.text = ""
                setDrawableFocusedFor5Digits()
            }
            6 -> {
                verification_1_textView.text = ""
                verification_2_textView.text = ""
                verification_3_textView.text = ""
                verification_4_textView.text = ""
                verification_5_textView.text = ""
                verification_6_textView.text = ""
                setDrawableFocusedFor6Digits()
            }
        }
    }

    /**
     * This method is used to add custom drawable to digits in every box when focused
     */
    private fun enableFocusedOption() {
        when (boxCount) {
            4 -> {
                setDrawableFocusedFor4Digits()
            }
            5 -> {
                setDrawableFocusedFor5Digits()
            }
            6 -> {
                setDrawableFocusedFor6Digits()
            }
        }
    }

    /**
     * This method is used to add custom drawable to 4-digits option in every box when focused
     */
    private fun setDrawableFocusedFor4Digits() {
        code_1_layout.background = if (verificationCodeText[0].toString().trim().isEmpty())
            mContext?.getDrawable(boxBG) else mContext?.getDrawable(boxBGFocused)

        code_2_layout.background = if (verificationCodeText[1].toString().trim().isEmpty())
            mContext?.getDrawable(boxBG) else mContext?.getDrawable(boxBGFocused)

        code_3_layout.background = if (verificationCodeText[2].toString().trim().isEmpty())
            mContext?.getDrawable(boxBG) else mContext?.getDrawable(boxBGFocused)

        code_4_layout.background = if (verificationCodeText[3].toString().trim().isEmpty())
            mContext?.getDrawable(boxBG) else mContext?.getDrawable(boxBGFocused)
    }

    /**
     * This method is used to add custom drawable to 5-digits option in every box when focused
     */
    private fun setDrawableFocusedFor5Digits() {
        code_1_layout.background = if (verificationCodeText[0].toString().trim().isEmpty())
            mContext?.getDrawable(boxBG) else mContext?.getDrawable(boxBGFocused)

        code_2_layout.background = if (verificationCodeText[1].toString().trim().isEmpty())
            mContext?.getDrawable(boxBG) else mContext?.getDrawable(boxBGFocused)

        code_3_layout.background = if (verificationCodeText[2].toString().trim().isEmpty())
            mContext?.getDrawable(boxBG) else mContext?.getDrawable(boxBGFocused)

        code_4_layout.background = if (verificationCodeText[3].toString().trim().isEmpty())
            mContext?.getDrawable(boxBG) else mContext?.getDrawable(boxBGFocused)

        code_5_layout.background = if (verificationCodeText[4].toString().trim().isEmpty())
            mContext?.getDrawable(boxBG) else mContext?.getDrawable(boxBGFocused)

    }

    /**
     * This method is used to add custom drawable to 6-digits option in every box when focused
     */
    private fun setDrawableFocusedFor6Digits() {
        code_1_layout.background = if (verificationCodeText[0].toString().trim().isEmpty())
            mContext?.getDrawable(boxBG) else mContext?.getDrawable(boxBGFocused)

        code_2_layout.background = if (verificationCodeText[1].toString().trim().isEmpty())
            mContext?.getDrawable(boxBG) else mContext?.getDrawable(boxBGFocused)

        code_3_layout.background = if (verificationCodeText[2].toString().trim().isEmpty())
            mContext?.getDrawable(boxBG) else mContext?.getDrawable(boxBGFocused)

        code_4_layout.background = if (verificationCodeText[3].toString().trim().isEmpty())
            mContext?.getDrawable(boxBG) else mContext?.getDrawable(boxBGFocused)

        code_5_layout.background = if (verificationCodeText[4].toString().trim().isEmpty())
            mContext?.getDrawable(boxBG) else mContext?.getDrawable(boxBGFocused)

        code_6_layout.background = if (verificationCodeText[5].toString().trim().isEmpty())
            mContext?.getDrawable(boxBG) else mContext?.getDrawable(boxBGFocused)
    }
}