package com.sar2016.panczuk.monstersgo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CatchingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CatchingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CatchingFragment extends Fragment {
    private static final int SWIPE_MIN_DISTANCE = 60;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ArrayList<SwipeMove> moves = new ArrayList();
    private int currentIndex;

    public CatchingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CatchingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CatchingFragment newInstance(String param1, String param2) {
        CatchingFragment fragment = new CatchingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_catching, container, false);
        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                           float velocityY) {
                        try {
                            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                //Left
                                swipeLeft();
                                return true;
                            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                //Right
                                swipeRight();
                                return true;
                            }
                            else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                                // UP
                                swipeUp();
                                return true;
                            } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                                //Down
                                swipeDown();
                                return true;
                            }
                        } catch (Exception e) {

                        }
                        return false;
                    }
                });

        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    public void onStart() {
        super.onStart();
        MainActivity activity = ((MainActivity) getActivity());

        LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.moves_wrapper);

        Monster m = activity.getSelectedMonster();
        for(int i = 0; i < m.getLevel(); i ++){
            SwipeMove move = new SwipeMove();
            this.moves.add(move);
            ImageView iv = new ImageView(getActivity().getApplicationContext());

            Bitmap b = ((BitmapDrawable)getActivity().getDrawable(move.getDrawableId())).getBitmap();
            Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 70, 70, false);

            iv.setImageDrawable(new BitmapDrawable(getResources(), bitmapResized));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            iv.setLayoutParams(lp);

            layout.addView(iv);
        }
        this.currentIndex = 0;
        ImageView ima = (ImageView)getActivity().findViewById(R.id.dinoPreview);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(((MainActivity)getActivity()).getSelectedMonster().getImageName(), "drawable", getActivity().getPackageName()));
        ima.setImageBitmap(bitmap);

        this.changeText(m.getName()+" (lvl :"+m.getLevel()+")");
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void swipeUp(){
        boolean b = this.moves.get(this.currentIndex).swipeUp();
        if(b){
            currentIndex++;
            if(currentIndex == moves.size())
                ((MainActivity)getActivity()).monsterGotCatched();
        }else{

        }
    }

    public void swipeDown(){
        boolean b = this.moves.get(this.currentIndex).swipeDown();
        if(b){
            currentIndex++;
            if(currentIndex == moves.size())
                ((MainActivity)getActivity()).monsterGotCatched();
        }else{

        }
    }

    public void swipeRight(){
        boolean b = this.moves.get(this.currentIndex).swipeRight();
        if(b){
            currentIndex++;
            if(currentIndex == moves.size())
                ((MainActivity)getActivity()).monsterGotCatched();
        }else{

        }
    }

    public void swipeLeft(){
        boolean b = this.moves.get(this.currentIndex).swipeLeft();
        if(b){
            currentIndex++;
            if(currentIndex == moves.size())
                ((MainActivity)getActivity()).monsterGotCatched();
        }else{

        }
    }

    public void changeText(String txt){
        TextView text = (TextView) getActivity().findViewById(R.id.test);
        text.setText(txt);
    }
}
