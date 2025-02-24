package com.backend.golvia.app.services.profile.asset;

import com.backend.golvia.app.dtos.profile.AssetDto;
import com.backend.golvia.app.entities.Asset;
import com.backend.golvia.app.entities.User;
import com.backend.golvia.app.entities.ChallengeEntry;
import com.backend.golvia.app.repositories.interactions.ChallengeEntryRepository;
import com.backend.golvia.app.repositories.profiles.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.Optional;

@Service
public class AssetService {

    @Autowired
    private final AssetRepository assetRepository;

    @Autowired
    private final ChallengeEntryRepository challengeEntryRepository;

    public AssetService(AssetRepository assetRepository, ChallengeEntryRepository challengeEntryRepository) {
        this.assetRepository = assetRepository;

        this.challengeEntryRepository = challengeEntryRepository;
    }

    @Transactional
    public Asset createAsset(User user, AssetDto assetDto) {
        Optional<Asset> existingAsset = assetRepository.findByUserId(user.getId());

        if (existingAsset.isPresent()) {
            Asset asset = existingAsset.get();

            if (assetDto.getProfilePictureUrl() != null && !assetDto.getProfilePictureUrl().isEmpty()) {
                asset.setProfilePictureUrl(assetDto.getProfilePictureUrl());
            }

            if (assetDto.getProfileReelUrl() != null && !assetDto.getProfileReelUrl().isEmpty()) {
                asset.setProfileReelUrl(assetDto.getProfileReelUrl());
            }

            if (assetDto.getCoverPhotoUrl() != null && !assetDto.getCoverPhotoUrl().isEmpty()) {
                asset.setCoverPhotoUrl(assetDto.getCoverPhotoUrl());
            }

            return assetRepository.save(asset);
        } else {
            Asset asset = new Asset();
            asset.setUser(user);
            asset.setProfilePictureUrl(assetDto.getProfilePictureUrl());
            asset.setProfileReelUrl(assetDto.getProfileReelUrl());
            asset.setCoverPhotoUrl(assetDto.getCoverPhotoUrl());

            return assetRepository.save(asset);
        }
    }

    public Optional<Asset> getAssetById(User user) {
        return assetRepository.findByUserId(user.getId());
    }

    @Transactional
    public void deleteAsset(Long id) {
        assetRepository.deleteById(id);
    }


    // New Update Method
    @Transactional
    public Optional<Asset> updateAsset(User user, AssetDto assetDto) throws NotFoundException {
        Optional<Asset>  optionalAsset= assetRepository.findByUserId(user.getId());

        if (optionalAsset.isPresent()) {

            Optional<ChallengeEntry> exUCE = challengeEntryRepository.findByUserEmail(user.getEmail());

            if (exUCE != null && exUCE.isPresent()) {
                ChallengeEntry challengeEntries = exUCE.get();
                challengeEntries.getUser().setProfileImageUrl(assetDto.getProfilePictureUrl());

                challengeEntryRepository.save(challengeEntries);
            }

            Asset existingAsset = optionalAsset.get();
            existingAsset.setUser(user);
            existingAsset.setProfilePictureUrl(assetDto.getProfilePictureUrl());
            existingAsset.setProfileReelUrl(assetDto.getProfileReelUrl());
            existingAsset.setCoverPhotoUrl(assetDto.getCoverPhotoUrl());

            Asset updatedAsset = assetRepository.save(existingAsset);
            return Optional.of(updatedAsset);
        }

        throw new NotFoundException("Asset Not Found");
    }
}