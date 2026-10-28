package com.swaraj.trading.Service;

import com.swaraj.trading.Model.Asset;
import com.swaraj.trading.Model.Coin;
import com.swaraj.trading.Model.User;
import org.springframework.stereotype.Service;

import java.util.List;

public interface AssetService {
    Asset createAsset(User user , Coin coin , double quantity);

    Asset getAssetById(Long assetId) throws Exception;

    Asset getAssetByUserIdAndId(Long userId , Long assetId);

    List<Asset> getUserAssets(Long userid);

    Asset updateAsset(Long assetId , double quantity) throws Exception;

    Asset getAssetByUserIdAndCoinId(Long userId , String coinId);

    void deleteAsset(Long assetID);

}
