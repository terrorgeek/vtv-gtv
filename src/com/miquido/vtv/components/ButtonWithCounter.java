/**
 * Created by Krzysztof Kogutkiewicz on 01.11.2012.
 *
 * Copyright 2012 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.vtv.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.Button;
import com.miquido.vtv.R;

import java.util.Random;

/**
 * Custom button with counter and icon
 *
 * @author Krzysztof Kogutkiewicz
 */
public class ButtonWithCounter extends Button {

  private Context context;

  public ButtonWithCounter(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
    prepareLayout();
    parseXmlAttributes(attrs);
  }

  private void prepareLayout() {
    this.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
    this.setTypeface(null, Typeface.BOLD);
    this.setTextColor(R.color.gray);
    Random r = new Random();
    int number = r.nextInt(2);
    if (number == 0) {
      this.setBackgroundResource(R.drawable.button_white);
    } else {
      this.setBackgroundResource(R.drawable.button_green);
    }

    this.setPadding(8, 0, 0, 0);
  }

  private void parseXmlAttributes(AttributeSet attrs) {
    TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ButtonWithCounter);

    final int N = attributes.getIndexCount();
    for (int i = 0; i < N; ++i) {
      int attr = attributes.getIndex(i);
      switch (attr) {
        case R.styleable.ButtonWithCounter_value:
          String value = attributes.getString(attr);
          this.setText(value);
          break;
        case R.styleable.ButtonWithCounter_type:
          if (attributes.getString(attr).equals("like")) {
            this.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_like), null, null, null);
          } else if (attributes.getString(attr).equals("dislike")) {
            this.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_dislike), null, null, null);
          } else if (attributes.getString(attr).equals("friends")) {
            this.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_friends), null, null, null);
          }
      }
    }
    attributes.recycle();
  }
}
