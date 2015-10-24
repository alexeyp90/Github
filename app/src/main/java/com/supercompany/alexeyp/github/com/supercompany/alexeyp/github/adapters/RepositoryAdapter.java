package com.supercompany.alexeyp.github.com.supercompany.alexeyp.github.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.supercompany.alexeyp.github.R;
import com.supercompany.alexeyp.github.activities.CreateRepositoryActivity;
import com.supercompany.alexeyp.github.activities.RepositoryActivity;
import com.supercompany.alexeyp.github.data.Repository;

import java.util.List;

/**
 * Adapter to show user list of repositories.
 */
public class RepositoryAdapter extends ArrayAdapter<Repository>{

    /**
     * List of repositories.
     */
    private List<Repository> list;

    /**
     * Activity context.
     */
    private Context context;

    public RepositoryAdapter(Context context, List<Repository> list) {
        super(context, 0, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.repository_item, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name_value);
            holder.owner = (TextView)convertView.findViewById(R.id.owner_value);
            holder.privateRepo = (CheckBox)convertView.findViewById(R.id.private_value);
            holder.description = (TextView)convertView.findViewById(R.id.description_value);
            holder.createdAt = (TextView)convertView.findViewById(R.id.created_at_value);
            holder.updatedAt = (TextView)convertView.findViewById(R.id.updated_at_value);
            holder.pushedAt = (TextView)convertView.findViewById(R.id.pushed_at_value);
            holder.homePage = (TextView)convertView.findViewById(R.id.homepage_value);
            holder.forks = (TextView)convertView.findViewById(R.id.forks_value);
            holder.watchers = (TextView)convertView.findViewById(R.id.watchers_value);
            holder.defaultBranch = (TextView)convertView.findViewById(R.id.default_branch_value);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Repository repo = list.get(position);

        holder.name.setText(repo.getName());
        holder.owner.setText(repo.getUserInfo().getLogin());
        holder.privateRepo.setChecked(repo.isPrivateRepo());
        holder.description.setText(repo.getDescription());
        holder.createdAt.setText(repo.getCreatedAt().toString());
        holder.updatedAt.setText(repo.getUpdatedAt().toString());
        holder.pushedAt.setText(repo.getPushedAt().toString());
        holder.homePage.setText(repo.getHomepage());
        holder.forks.setText(String.valueOf(repo.getForks()));
        holder.watchers.setText(String.valueOf(repo.getWatchers()));
        holder.defaultBranch.setText(repo.getDefaultBranch());

        return convertView;
    }

    static class ViewHolder {
        private TextView name;
        private TextView owner;
        private CheckBox privateRepo;
        private TextView description;
        private TextView createdAt;
        private TextView updatedAt;
        private TextView pushedAt;
        private TextView homePage;
        private TextView forks;
        private TextView watchers;
        private TextView defaultBranch;
    }
}
