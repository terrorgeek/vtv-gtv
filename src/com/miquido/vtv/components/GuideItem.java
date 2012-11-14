package com.miquido.vtv.components;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miquido.vtv.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Guide item component
 *
 * @author miquido.com
 */
public class GuideItem extends LinearLayout {

  private static final Logger log = LoggerFactory.getLogger(GuideItem.class);

  private TextView programTitle;

  private TextView programTime;

  boolean isSelected;

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean selected) {
    isSelected = selected;
  }

  public void setProgramTitle(String programTitle) {
    this.programTitle.setText(programTitle);
  }

  public String getProgramTitle() {
    return programTitle.getText().toString();
  }

  public void setProgramTime(String programTime) {
    this.programTime.setText(programTime);
  }

  public String getProgramTime() {
    return programTime.getText().toString();
  }

  public void setWidth(int pixels) {
    //TODO Should be done in DIP in the future
    this.setLayoutParams(new LayoutParams(pixels, ViewGroup.LayoutParams.WRAP_CONTENT));
    this.setBackgroundDrawable(getResources().getDrawable(R.drawable.guide_item_background_selector));
  }

  public GuideItem(Context context) {
    super(context);
    inflateLayout(context);
  }

  private void inflateLayout(Context context) {
    final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.guide_item, this);
    this.programTitle = (TextView) findViewById(R.id.programTitle);
    this.programTime = (TextView) findViewById(R.id.programTime);
    this.setClickable(true);
    this.setFocusable(true);
  }

  @Override
  protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
    if (gainFocus) {
      if (!this.isSelected)
        this.setBackgroundDrawable(getResources().getDrawable(R.drawable.guide_item_background_focused));
    } else {
      if (!this.isSelected)
        this.setBackgroundDrawable(getResources().getDrawable(R.drawable.guide_item_background));
    }

  }
}
