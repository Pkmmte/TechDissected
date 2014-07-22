package com.pkmmte.techdissected.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.model.Article;
import com.pkmmte.techdissected.util.Utils;
import com.pkmmte.techdissected.view.PkImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class FeedAdapter extends BaseAdapter {
	private Context mContext;
	private List<Article> mFeed;
	private OnArticleClickListener mListener;

	public FeedAdapter(Context context) {
		this.mContext = context;
		this.mFeed = new ArrayList<Article>();
	}
	public FeedAdapter(Context context, List<Article> feed) {
		this.mContext = context;
		this.mFeed = feed;
	}

	public void updateFeed(List<Article> feed) {
		this.mFeed = feed;
		notifyDataSetChanged();
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
			holder.btnCategory = (Button) convertView.findViewById(R.id.btnCategory);
			holder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
			holder.txtDescription = (TextView) convertView.findViewById(R.id.txtDescription);
			holder.txtAuthor = (TextView) convertView.findViewById(R.id.txtAuthor);
			holder.txtDate= (TextView) convertView.findViewById(R.id.txtDate);

			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder) convertView.getTag();

		Picasso.with(mContext).load(mArticle.getImage()).placeholder(R.drawable.placeholder).into(holder.imgPreview);

		holder.btnCategory.setText(mArticle.getCategory());
		holder.txtTitle.setText(mArticle.getTitle());
		holder.txtDescription.setText(mArticle.getDescription());
		holder.txtAuthor.setText("By " + mArticle.getAuthor());
		holder.txtDate.setText(Utils.getRelativeDate(mArticle.getDate()));

		holder.mCard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e("ROAR", "Clicked card... " + (mListener == null));
				if(mListener != null)
					mListener.onClick(mArticle);
			}
		});

		return convertView;
	}

	public interface OnArticleClickListener {
		public void onClick(Article article);
		public void onCategoryClick(String category);
	}

	private class ViewHolder {
		public View mCard;
		public ImageView imgPreview;
		public Button btnCategory;
		public TextView txtTitle;
		public TextView txtDescription;
		public TextView txtAuthor;
		public TextView txtDate;
	}
}