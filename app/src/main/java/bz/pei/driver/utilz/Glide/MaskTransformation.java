package bz.pei.driver.utilz.Glide;

/**
 * Copyright (C) 2017 Wasabeef
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import bz.pei.driver.utilz.CommonUtils;

public class MaskTransformation extends BitmapTransformation {

  private static Paint paint = new Paint();
  private int maskId;

  static {
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
  }

  /**
   * @param maskId If you change the mask file, please also rename the mask file, or Glide will get
   * the cache with the old mask. Because key() return the same values if using the
   * same make file name. If you have a good idea please tell us, thanks.
   */
  public MaskTransformation(int maskId) {
    this.maskId = maskId;
  }

  @Override protected Bitmap transform(@NonNull Context context, @NonNull BitmapPool pool,
      @NonNull Bitmap toTransform, int outWidth, int outHeight) {
    int width = toTransform.getWidth();
    int height = toTransform.getHeight();

    Bitmap bitmap = pool.get(width, height, Bitmap.Config.ARGB_8888);
    bitmap.setHasAlpha(true);

    Drawable mask = CommonUtils.getMaskDrawable(context.getApplicationContext(), maskId);

    Canvas canvas = new Canvas(bitmap);
    mask.setBounds(0, 0, width, height);
    mask.draw(canvas);
    canvas.drawBitmap(toTransform, 0, 0, paint);

    return bitmap;
  }

  @Override public String key() {
    return "MaskTransformation(maskId=" + maskId + ")";
  }
}
