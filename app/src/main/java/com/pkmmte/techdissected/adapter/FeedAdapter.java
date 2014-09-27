package com.pkmmte.techdissected.adapter;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.pkmmte.pkrss.Article;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.util.Constants;
import com.pkmmte.techdissected.util.Utils;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class FeedAdapter extends BaseAdapter {
	private Context mContext;
	private List<Article> mFeed;
	private ColorMatrixColorFilter mFilter;
	private OnArticleClickListener mListener;
	private boolean grayscaleRead;

	public FeedAdapter(Context context) {
		this(context, new ArrayList<Article>());
	}

	public FeedAdapter(Context context, List<Article> feed) {
		this.mContext = context;
		this.mFeed = feed;
		ColorMatrix grayscaleFilter = new ColorMatrix();
		grayscaleFilter.setSaturation(0);
		this.mFilter = new ColorMatrixColorFilter(grayscaleFilter);
		try {
			this.grayscaleRead = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE).getBoolean(Constants.PREF_READ, false);
		} catch (Exception e) {
			this.grayscaleRead = true;
		}
	}

	public void updateFeed(List<Article> feed) {
		this.mFeed = feed;
		this.notifyDataSetChanged();
	}

	public void setOnClickListener(OnArticleClickListener listener) {
		this.mListener = listener;
	}

	@Override
	public int getCount() {
		return mFeed.size();
	}

	@Override
	public Article getItem(int position) {
		return mFeed.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Article mArticle = mFeed.get(position);
		ViewHolder holder;

		if(convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.fragment_feed_article, parent, false);

			holder = new ViewHolder();
			holder.mCard = convertView.findViewById(R.id.card);
			holder.imgPreview = (ImageView) convertView.findViewById(R.id.imgPreview);
			holder.imgFavorite = (ImageView) convertView.findViewById(R.id.imgFavorite);
			holder.txtTag = (TextView) convertView.findViewById(R.id.txtTag);
			holder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
			holder.txtDescription = (TextView) convertView.findViewById(R.id.txtDescription);
			holder.txtAuthor = (TextView) convertView.findViewById(R.id.txtAuthor);
			holder.txtDate= (TextView) convertView.findViewById(R.id.txtDate);
			holder.currentTag = 0;

			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder) convertView.getTag();

		Picasso.with(mContext).load(mArticle.getImage()).placeholder(R.drawable.placeholder).into(holder.imgPreview);

		if(mArticle.getTags().size() > 0) {
			holder.txtTag.setVisibility(View.VISIBLE);
			holder.txtTag.setText(mArticle.getTags().get(0));
		}
		else
			holder.txtTag.setVisibility(View.GONE);
		holder.txtTitle.setText(mArticle.getTitle());
		holder.txtDescription.setText(mArticle.getDescription());
		holder.txtAuthor.setText("By " + mArticle.getAuthor());
		holder.txtDate.setText(Utils.getRelativeDate(mArticle.getDate()));

		holder.mCard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mListener != null)
					mListener.onClick(mArticle);
			}
		});
		holder.imgFavorite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean favorite = !mArticle.isFavorite();
				mArticle.saveFavorite(favorite);

				if(mListener != null)
					mListener.onAddFavorite(mArticle, favorite);

				notifyDataSetChanged();
			}
		});

		if(grayscaleRead && mArticle.isRead())
			holder.imgPreview.setColorFilter(mFilter);
		else
			holder.imgPreview.clearColorFilter();

		holder.imgFavorite.setImageResource(mArticle.isFavorite() ? R.drawable.selector_favorite_full : R.drawable.selector_favorite_empty);

		return convertView;
	}

	public interface OnArticleClickListener {
		public void onClick(Article article);
		public void onAddFavorite(Article article, boolean favorite);
	}

	private class ViewHolder {
		public View mCard;
		public ImageView imgPreview;
		public ImageView imgFavorite;
		public TextView txtTag;
		public TextView txtTitle;
		public TextView txtDescription;
		public TextView txtAuthor;
		public TextView txtDate;
		public int currentTag;
	}
}