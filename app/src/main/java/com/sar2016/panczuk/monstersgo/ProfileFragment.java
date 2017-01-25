package com.sar2016.panczuk.monstersgo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if(v instanceof ImageView){
            ImageView view = (ImageView)v;
            ((MainActivity)getActivity()).setLoggedUserImage(view.getDrawable());
            ImageView imageView = (ImageView) getActivity().findViewById(R.id.player_image);
            imageView.setImageBitmap(((BitmapDrawable)view.getDrawable()).getBitmap());
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity activity = ((MainActivity)getActivity());
        TextView text = (TextView)getActivity().findViewById(R.id.ranger_name);
        text.setText(activity.getLoggedUser());

        ImageView imageView = (ImageView) getActivity().findViewById(R.id.player_image);
        imageView.setImageBitmap(((BitmapDrawable)activity.getLoggedUserImage()).getBitmap());

        activity.findViewById(R.id.ranger_image_select).setOnClickListener(this);
        activity.findViewById(R.id.rangera_image_select).setOnClickListener(this);
        activity.findViewById(R.id.ninja_image_select).setOnClickListener(this);
        activity.findViewById(R.id.ninjette_image_select).setOnClickListener(this);
        activity.findViewById(R.id.santa_image_select).setOnClickListener(this);
    }
}
