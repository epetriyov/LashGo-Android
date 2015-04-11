package com.lashgo.android.ui.check;

import com.lashgo.model.dto.CheckDto;

/**
 * Created by Eugene on 30.11.2014.
 */
public interface ICheckFragment {

    void hideSendPhotoBtn();

    void updateImage(String imgPath);

    void updateCheckDto(CheckDto checkDto);
}
