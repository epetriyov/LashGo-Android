package com.lashgo.mobile.ui.check;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import com.lashgo.mobile.R;
import com.lashgo.mobile.service.handlers.BaseIntentHandler;
import com.lashgo.mobile.ui.BaseActivity;
import com.lashgo.mobile.ui.BaseFragment;
import com.lashgo.mobile.ui.ImageAnimation;
import com.lashgo.mobile.utils.PhotoUtils;
import com.lashgo.model.dto.VoteAction;
import com.lashgo.model.dto.VotePhoto;

import java.util.ArrayList;

/**
 * Created by Eugene on 23.10.2014.
 */
public class VoteFragment extends BaseFragment implements View.OnClickListener, VotePhotoController.VotePhotoListener {

    private ArrayList<VotePhoto> votePhotos;
    private ScreenState screenState;
    private ImageView voteButton;
    private TextView voteHint;
    private TextView photosCounter;
    private VotePhotoController firstPhotoController;
    private VotePhotoController secondPhotoController;
    private VotePhotoController thirdPhotoController;
    private VotePhotoController fourthPhotoController;
    private View baloonHint;
    private ImageAnimation imageAnimation;
    private ImageView expandedImageView;
    private int position;
    private int totalSize;
    private View voteGallery;
    private boolean voteDone;
    private int selectedPhoto;
    private int checkId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.adt_vote, container, false);
        expandedImageView = (ImageView) view.findViewById(R.id.expanded_image);
        imageAnimation = new ImageAnimation(getActivity(), view.findViewById(R.id.shadow), view.findViewById(R.id.container), expandedImageView);
        initViews(view);
        return view;
    }

    @Override
    public void refresh() {

    }

    @Override
    public void clearOtherChecks(VotePhotoController votePhotoController) {
        voteHint.setText(R.string.now_this_button);
        if (firstPhotoController != votePhotoController) {
            firstPhotoController.clearCheck();
        }
        if (secondPhotoController != votePhotoController) {
            secondPhotoController.clearCheck();
        }
        if (thirdPhotoController != votePhotoController) {
            thirdPhotoController.clearCheck();
        }
        if (fourthPhotoController != votePhotoController) {
            fourthPhotoController.clearCheck();
        }
    }

    @Override
    public void openPhotoActivity(VotePhotoController votePhotoController) {
        int photoPosition = 0;
        if (votePhotoController.equals(firstPhotoController)) {
            photoPosition = 0;
        } else if (votePhotoController.equals(secondPhotoController)) {
            photoPosition = 1;
        } else if (votePhotoController.equals(thirdPhotoController)) {
            photoPosition = 2;
        } else if (votePhotoController.equals(fourthPhotoController)) {
            photoPosition = 3;
        }
        PhotoUtils.displayFullImage(getActivity(), expandedImageView, PhotoUtils.getFullPhotoUrl(votePhotos.get(photoPosition).getPhotoDto().getUrl()));
        imageAnimation.zoomImageFromThumb(votePhotoController.getImageView(), getResources().getInteger(android.R.integer.config_shortAnimTime));
    }

    @Override
    public void showOtherShadows(VotePhotoController votePhotoController) {
        if (firstPhotoController != votePhotoController) {
            firstPhotoController.showShadow();
        }
        if (secondPhotoController != votePhotoController) {
            secondPhotoController.showShadow();
        }
        if (thirdPhotoController != votePhotoController) {
            thirdPhotoController.showShadow();
        }
        if (fourthPhotoController != votePhotoController) {
            fourthPhotoController.showShadow();
        }
    }

    @Override
    public void setFirstHint() {
        voteHint.setText(R.string.choose_photo);
    }

    @Override
    protected void registerActionsListener() {
        super.registerActionsListener();
        addActionListener(BaseIntentHandler.ServiceActionNames.ACTION_VOTE.name());
    }

    @Override
    protected void unregisterActionsListener() {
        super.unregisterActionsListener();
        removeActionListener(BaseIntentHandler.ServiceActionNames.ACTION_VOTE.name());
    }

    @Override
    public void startProgress() {
        showOverlayProgress();
    }

    @Override
    public void stopProgress() {
        hideOverlayProgress();
    }

    private void initViews(final View view) {
        voteGallery = view.findViewById(R.id.vote_gallery_layout);
        voteDone = votePhotos.get(0).isShown();
        voteButton = (ImageView) view.findViewById(R.id.vote_button);
        voteButton.setOnClickListener(this);
        baloonHint = view.findViewById(R.id.baloon_hint);
        if (settingsHelper.alreadyVoted()) {
            baloonHint.setVisibility(View.GONE);
        }
        voteHint = (TextView) view.findViewById(R.id.vote_hint);
        photosCounter = (TextView) view.findViewById(R.id.checks_counter);
        screenState = ScreenState.CHOOSE_PHOTO;
        updateCounter();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (voteGallery.getHeight() > 0) {
            initVoteGallery();
        } else {
            voteGallery.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {

                        @Override
                        public void onGlobalLayout() {
                            if (voteGallery.getHeight() > 0) {
                                initVoteGallery();
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                    voteGallery.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                } else {
                                    voteGallery.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                }
                            }
                        }

                    });
        }
    }

    private void initVoteGallery() {
        int imageHeight = voteGallery.getHeight() / 2;
        int imageWidth = (PhotoUtils.getScreenWidth(getActivity()) - 20) / 2;
        firstPhotoController = new VotePhotoController(getActivity(), VoteFragment.this, getView(), R.id.first_vote_photo_layout, R.id.first_vote_photo, R.id.first_photo_check, R.id.first_photo_shadow, imageWidth, imageHeight, votePhotos.size() > 0 ? votePhotos.get(0) : null);
        secondPhotoController = new VotePhotoController(getActivity(), VoteFragment.this, getView(), R.id.second_vote_photo_layout, R.id.second_vote_photo, R.id.second_photo_check, R.id.second_photo_shadow, imageWidth, imageHeight, votePhotos.size() > 1 ? votePhotos.get(1) : null);
        thirdPhotoController = new VotePhotoController(getActivity(), VoteFragment.this, getView(), R.id.third_vote_photo_layout, R.id.third_vote_photo, R.id.third_photo_check, R.id.third_photo_shadow, imageWidth, imageHeight, votePhotos.size() > 2 ? votePhotos.get(2) : null);
        fourthPhotoController = new VotePhotoController(getActivity(), VoteFragment.this, getView(), R.id.fourth_vote_photo_layout, R.id.fourth_vote_photo, R.id.fourth_photo_check, R.id.fourth_photo_shadow, imageWidth, imageHeight, votePhotos.size() > 3 ? votePhotos.get(3) : null);
        if (voteDone) {
            if (votePhotos.size() > 0 && votePhotos.get(0).isVoted()) {
                firstPhotoController.setIsChecked(true);
            }
            if (votePhotos.size() > 1 && votePhotos.get(1).isVoted()) {
                secondPhotoController.setIsChecked(true);
            }
            if (votePhotos.size() > 2 && votePhotos.get(2).isVoted()) {
                thirdPhotoController.setIsChecked(true);
            }
            if (votePhotos.size() > 3 && votePhotos.get(3).isVoted()) {
                fourthPhotoController.setIsChecked(true);
            }
            voteDone();
            voteButton.setVisibility(View.GONE);
            baloonHint.setVisibility(View.GONE);
        }

    }

    private void voteDone() {
        firstPhotoController.voteDone();
        secondPhotoController.voteDone();
        thirdPhotoController.voteDone();
        fourthPhotoController.voteDone();
    }

    private void updateCounter() {
        if (totalSize - position * 4 + 4 < 2) {
            photosCounter.setText(String.valueOf(totalSize));
        } else {
            photosCounter.setText(String.format("%d-%d/%d", position * 4 + 1, position * 4 + votePhotos.size(), totalSize));
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.vote_button) {
            if (screenState.equals(ScreenState.CHOOSE_PHOTO)) {
                selectedPhoto = 0;
                if (firstPhotoController.isChecked()) {
                    selectedPhoto = 1;
                } else if (secondPhotoController.isChecked()) {
                    selectedPhoto = 2;
                } else if (thirdPhotoController.isChecked()) {
                    selectedPhoto = 3;
                } else if (fourthPhotoController.isChecked()) {
                    selectedPhoto = 4;
                }
                if (selectedPhoto > 0) {
                    serviceHelper.votePhoto(buildVoteAction(selectedPhoto));
                }
            } else {
                baloonHint.setVisibility(View.GONE);
                voteButton.setVisibility(View.GONE);
                ((VoteProcessActivity) getActivity()).continueVoting();
            }
        }
    }

    private VoteAction buildVoteAction(int selectedPhoto) {
        VoteAction voteAction = new VoteAction();
        if (votePhotos != null) {
            voteAction.setVotedPhotoId(votePhotos.get(selectedPhoto - 1).getPhotoDto().getId());
            long[] photoIds = new long[votePhotos.size()];
            for (int i = 0; i < votePhotos.size(); i++) {
                photoIds[i] = votePhotos.get(i).getPhotoDto().getId();
            }
            voteAction.setPhotoIds(photoIds);
            voteAction.setCheckId(checkId);
        }
        return voteAction;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            votePhotos = (ArrayList<VotePhoto>) savedInstanceState.getSerializable(BaseActivity.ExtraNames.VOTE_PHOTOS.name());
            position = savedInstanceState.getInt(BaseActivity.ExtraNames.POSITION.name());
            totalSize = savedInstanceState.getInt(BaseActivity.ExtraNames.SIZE.name());
            checkId = savedInstanceState.getInt(BaseActivity.ExtraNames.CHECK_ID.name());
            voteDone = savedInstanceState.getBoolean(BaseActivity.ExtraNames.VOTE_DONE.name());
            selectedPhoto = savedInstanceState.getInt(BaseActivity.ExtraNames.SELECTED_PHOTO.name());
        } else {
            Bundle args = getArguments();
            if (args != null) {
                votePhotos = (ArrayList<VotePhoto>) args.getSerializable(BaseActivity.ExtraNames.VOTE_PHOTOS.name());
                position = args.getInt(BaseActivity.ExtraNames.POSITION.name());
                totalSize = args.getInt(BaseActivity.ExtraNames.SIZE.name());
                checkId = args.getInt(BaseActivity.ExtraNames.CHECK_ID.name());
                voteDone = args.getBoolean(BaseActivity.ExtraNames.VOTE_DONE.name());
                selectedPhoto = args.getInt(BaseActivity.ExtraNames.SELECTED_PHOTO.name());
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(BaseActivity.ExtraNames.VOTE_PHOTOS.name(), votePhotos);
        outState.putInt(BaseActivity.ExtraNames.POSITION.name(), position);
        outState.putInt(BaseActivity.ExtraNames.SIZE.name(), totalSize);
        outState.putInt(BaseActivity.ExtraNames.CHECK_ID.name(), checkId);
        outState.putInt(BaseActivity.ExtraNames.SELECTED_PHOTO.name(),selectedPhoto);
        outState.putBoolean(BaseActivity.ExtraNames.VOTE_DONE.name(),voteDone);
        super.onSaveInstanceState(outState);
    }

    public static Fragment newInstance(int checkId, ArrayList<VotePhoto> votePhotoList, int position, int totalSize) {
        VoteFragment voteFragment = new VoteFragment();
        Bundle args = new Bundle();
        args.putSerializable(BaseActivity.ExtraNames.VOTE_PHOTOS.name(), votePhotoList);
        args.putInt(BaseActivity.ExtraNames.CHECK_ID.name(), checkId);
        args.putInt(BaseActivity.ExtraNames.POSITION.name(), position);
        args.putInt(BaseActivity.ExtraNames.SIZE.name(), totalSize);
        voteFragment.setArguments(args);
        return voteFragment;
    }

    private enum ScreenState {CHOOSE_PHOTO, NEXT_PHOTOS}

    @Override
    public void processServerResult(String action, int resultCode, Bundle data) {
        super.processServerResult(action, resultCode, data);
        if (resultCode == BaseIntentHandler.SUCCESS_RESPONSE) {
            if (BaseIntentHandler.ServiceActionNames.ACTION_VOTE.name().equals(action)) {
                updateVotePhotos();
                voteDone();
                settingsHelper.firstVote();
                screenState = ScreenState.NEXT_PHOTOS;
                voteButton.setImageResource(R.drawable.btn_next_vote);
                voteHint.setText(R.string.go_to_next_partition);
            }
        }
    }

    private void updateVotePhotos() {
        if (votePhotos != null) {
            for (VotePhoto votePhoto : votePhotos) {
                votePhoto.setShown(true);
            }
            if (selectedPhoto > 0) {
                votePhotos.get(selectedPhoto - 1).setVoted(true);
            }
        }
    }

}
