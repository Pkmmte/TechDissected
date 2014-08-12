package com.pkmmte.techdissected.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.pkmmte.techdissected.R;
import com.pkmmte.techdissected.model.Author;
import com.pkmmte.techdissected.model.CreditsLibraryItem;
import com.pkmmte.techdissected.util.RoundTransform;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class AuthorAdapter extends BaseAdapter{
	private Context mContext;
	private List<Author> authorList;

	public AuthorAdapter(Context context)
	{
		this.mContext = context;
		this.authorList = new ArrayList<Author>();
	}

	public AuthorAdapter(Context context, List<Author> authors)
	{
		this.mContext = context;
		this.authorList = authors;
	}

	public void addItem(Author author)
	{
		this.authorList.add(author);
	}

	@Override
	public int getCount()
	{
		return authorList.size();
	}

	@Override
	public Author getItem(int position)
	{
		return authorList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		final Author mAuthor = authorList.get(position);
		ViewHolder holder;

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.fragment_about_author, parent, false);

			holder = new ViewHolder();
			holder.imgAvatar = (CircularImageView) convertView.findViewById(R.id.imgAvatar);
			holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
			holder.txtDescription = (TextView) convertView.findViewById(R.id.txtDescription);

			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}

		Picasso.with(mContext).load(mAuthor.getAvatar()).error(R.drawable.dev_avatar).into(
			holder.imgAvatar);
		holder.txtName.setText(mAuthor.getName());
		holder.txtDescription.setText(mAuthor.getDescription());

		return convertView;
	}

	private class ViewHolder {
		public CircularImageView imgAvatar;
		public TextView txtName;
		public TextView txtDescription;
	}
}