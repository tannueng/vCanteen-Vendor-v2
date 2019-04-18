package com.example.vcanteenvendor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ReviewListAdapter extends ArrayAdapter {

    TextView ratingScore;
    TextView reviewDate;
    TextView commentText;
    View customView;

    private ReviewList mReviewList;
    private List<Review> mReviewArrayList;

    Review singleReview;

    ReviewListAdapter(Context context, ReviewList List){
        super(context, R.layout.review_list_item, List.reviewList);
        mReviewList = List;
        mReviewArrayList = List.reviewList;
    }

    @Override

    public View getView(final int position, View convertView, ViewGroup parent){
        LayoutInflater orderInflater = LayoutInflater.from(getContext());
        customView = orderInflater.inflate(R.layout.review_list_item, parent, false);

        singleReview = (Review) getItem(position);

        ratingScore = customView.findViewById(R.id.ratingScore);
        reviewDate = customView.findViewById(R.id.reviewDate);
        commentText = customView.findViewById(R.id.commentText);






        ratingScore.setText(String.valueOf(singleReview.getScore()));
        reviewDate.setText(singleReview.getCreatedAt());
        commentText.setText(singleReview.getComment());
        return customView;
    }
}
