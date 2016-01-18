package com.pkmmte.techdissected.adapter;

import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pkmmte.pkrss.Article;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.util.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
	// Data set
	private final List<Article> articles = new ArrayList<>();
	private final ArticleListener listener;

	// Read properties
	private boolean grayscaleEnabled;
	private ColorMatrixColorFilter grayscaleFilter;

	public FeedAdapter(@Nullable ArticleListener listener) {
		this.listener = listener;
	}

	public void setArticles(@NonNull List<Article> articles) {
		this.articles.clear();
		this.articles.addAll(articles);
	}

	public void setGrayscaleEnabled(boolean enabled) {
		this.grayscaleEnabled = enabled;
		this.grayscaleFilter = enabled ? Utils.getGrayscaleFilter() : null;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_feed_article, parent, false));
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		final Article article = articles.get(position);
		final Context context = holder.itemView.getContext();

		Picasso.with(context)
				.load(article.getImage())
				.placeholder(R.drawable.placeholder)
				.into(holder.imgPreview);

		if(article.getTags().size() > 0) {
			holder.txtTag.setVisibility(View.VISIBLE);
			holder.txtTag.setText(article.getTags().get(0));
		}
		else
			holder.txtTag.setVisibility(View.GONE);

		holder.txtTitle.setText(article.getTitle());
		holder.txtDescription.setText(article.getDescription());
		holder.txtAuthor.setText("By " + article.getAuthor());
		holder.txtDate.setText(Utils.getRelativeDate(article.getDate()));

		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(listener != null)
					listener.onArticleClick(article);
			}
		});
		holder.imgFavorite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean favorite = !article.isFavorite();
				article.saveFavorite(favorite);

				if(listener != null)
					listener.onArticleFavorite(article, favorite);

				notifyDataSetChanged();
			}
		});

		if(grayscaleEnabled && article.isRead())
			holder.imgPreview.setColorFilter(grayscaleFilter);
		else
			holder.imgPreview.clearColorFilter();

		holder.imgFavorite.setImageResource(article.isFavorite() ? R.drawable.selector_favorite_full : R.drawable.selector_favorite_empty);
	}

	@Override
	public int getItemCount() {
		return articles.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		final ImageView imgPreview;
		final ImageView imgFavorite;
		final TextView txtTag;
		final TextView txtTitle;
		final TextView txtDescription;
		final TextView txtAuthor;
		final TextView txtDate;

		public ViewHolder(View itemView) {
			super(itemView);
			imgPreview = (ImageView) itemView.findViewById(R.id.imgPreview);
			imgFavorite = (ImageView) itemView.findViewById(R.id.imgFavorite);
			txtTag = (TextView) itemView.findViewById(R.id.txtTag);
			txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
			txtDescription = (TextView) itemView.findViewById(R.id.txtDescription);
			txtAuthor = (TextView) itemView.findViewById(R.id.txtAuthor);
			txtDate= (TextView) itemView.findViewById(R.id.txtDate);
		}
	}

	public interface ArticleListener {
		void onArticleClick(Article article);
		void onArticleFavorite(Article article, boolean favorite);
	}
}
