package com.fileexplorer;

import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
	private final WeakReference<Container> imageViewReference;
	private String r = "";
	private String type = "";
	private Container con = null;

	public BitmapWorkerTask(Container container, String r, String type) {
		// Use a WeakReference to ensure the ImageView can be garbage collected
		imageViewReference = new WeakReference<Container>(container);
		this.r = r;
		this.type = type;
		this.con = container;
	}

	public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		int height = options.outHeight;
		int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			int halfHeight = height / 2;
			int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}

	// Decode image in background.
	@SuppressLint("NewApi")
	@Override
	protected Bitmap doInBackground(Integer... params) {
		if (type.equals("video")) {
			if (android.os.Build.VERSION.SDK_INT > 7)
				return ThumbnailUtils.createVideoThumbnail(r, MediaStore.Images.Thumbnails.MINI_KIND);
			return MediaStore.Video.Thumbnails.getThumbnail(con.act.getContentResolver(), con.img.getId(), MediaStore.Video.Thumbnails.MICRO_KIND, null);
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(r, options);
		options.inSampleSize = calculateInSampleSize(options, 64, 64);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(r, options);
	}

	// Once complete, see if ImageView is still around and set bitmap.
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (imageViewReference != null && bitmap != null) {
			final Container imageView = imageViewReference.get();
			if (imageView != null && imageView.img != null) {
				imageView.img.setImageBitmap(bitmap);
			}
		}
	}
}