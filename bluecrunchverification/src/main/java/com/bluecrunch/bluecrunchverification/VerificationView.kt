package com.bluecrunch.bluecrunchverification

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.bluecrunch.bluecrunchverification.databinding.VerificationViewBinding
import kotlinx.android.synthetic.main.verification_view.view.*


class VerificationView : ConstraintLayout {

    private var mContext: Context? = null
    private var binding: VerificationViewBinding? = null
    var boxCount = 4
    var boxBG = 0
    var boxHeight = 0f
    var boxTextColor = 0
    var boxSpace = 0
    var verificationCodeText = ""
    var textCodeObservables =
        arrayOfNulls<ObservableField<String>>(0)

    /** Constructor with context **/
    constructor(context: Context?) : super(context) {
        mContext = context
        inflate()
        bindViews()
        setNumberOfDigits()
        initTextCodeObservables()
        listenForTextChanges()
    }

    /** Constructor with context and attributes **/
    @SuppressLint("Recycle")
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        mContext = context
        initStyleables(attrs)
        inflate()
        bindViews()
        setNumberOfDigits()
        initTextCodeObservables()
        listenForTextChanges()
    }

    /** Init styles attrs from styleable file **/
    private fun initStyleables(attrs: AttributeSet?) {
        val typeArr = context?.obtainStyledAttributes(
            attrs
            , R.styleable.VerificationView, 0, 0
        )
        boxCount = typeArr!!.getInt(R.styleable.VerificationView_box_count, 4)
        boxBG = typeArr.getResourceId(
            R.styleable.VerificationView_box_background,
            R.drawable.cornered_shape
        )
        boxHeight = typeArr.getDimension(R.styleable.VerificationView_box_height, 50f)
        boxTextColor = typeArr.getColor(
            R.styleable.VerificationView_box_text_color,
            mContext?.getColor(android.R.color.black)!!
        )
        boxSpace = typeArr.getDimension(R.styleable.VerificationView_box_space, 10f)
            .toInt()

        textCodeObservables = arrayOfNulls(boxCount)
        typeArr.recycle()
    }

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

    /** Init number of digits for code layout **/
    private fun setNumberOfDigits() {
        binding?.isFourDigit = boxCount == 4
        binding?.isSixDigit = boxCount == 6
    }

    /** Inflate verification layout **/
    private fun inflate() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.verification_view, this, true
        )
        binding?.view = this
    }

    /** Init text codes for 1st time **/
    private fun initTextCodeObservables() {
        for (i in 0 until boxCount) {
            textCodeObservables[i] = ObservableField("")
        }
    }

    /** Listen for text changes in code editText **/
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
                for (j in verificationCodeText.indices) {
                    textCodeObservables[j]?.set("${verificationCodeText[j]}")
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }
}