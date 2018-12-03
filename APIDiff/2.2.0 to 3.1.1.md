## TuSDKShortVideo-Android 3.1.0 版本

##### Added API:

* `TuSdkMovieEditor`
* `TuSdkEditorTranscoder`
* `TuSdkEditorPlayer`
* `TuSdkEditorEffector`
* `TuSdkEditorAudioMixer`
* `TuSdkEditorSaver`
* `TuSdkMovieEditorOptions`
* `TuSdkMediaDataSource`
* `TuSdkTimeRange`
* `TuSdkMediaEffectData`
* `TuSdkMediaAudioEffectData`
* `TuSdKMediaComicEffectData`
* `TuSdKMediaFilterEffectData`
* `TuSdkMediaParticleEffectData`
* `TuSdKMediaSceneEffectData`
* `TuSdKMediaStickerAudioEffectData`
* `TuSdKMediaStickerEffectData`
* `TuSdKMediaTextEffectData`
* `TuSdKMediaTimeEffect`
* `TuSdKMediaRepeatTimeEffect`
* `TuSdKMediaReversalTimeEffect`
* `TuSdKMediaSlowTimeEffect`

##### Removed API:

* `TuSDKMovieEditor` （由 `TuSdkMovieEditor` 替代)
* `TuSDKMovieEditorOptions` （由 `TuSdkMovieEditor` 替代）
* `TuSDKMediaDataSource` （由 `TuSdkMediaDataSource` 替代）
* `TuSDKTimeRange` (由 `TuSdkTimeRange` 替代)
* `TuSDkMediaEffectData` (由 `TuSdkMediaEffectData` 替代）
* `TuSDkMediaAudioEffectData` (由 `TuSdkMediaAudioEffectData` 替代）
* `TuSDKMediaFilterEffectData` (由 `TuSdKMediaFilterEffectData` 替代）
* `TuSDkMediaParticleEffectData` (由 `TuSdkMediaParticleEffectData `替代）
* `TuSDKMediaSceneEffectData` (由 `TuSdKMediaSceneEffectData` 替代）
* `TuSDKMediaStickerAudioEffectData` (由 `TuSdKMediaStickerAudioEffectData` 替代）
* `TuSDKMediaStickerEffectData`  (由 `TuSdKMediaStickerEffectData` 替代）
* `TuSDKMediaTextEffectData` (由 `TuSdKMediaTextEffectData` 替代）
* `TuSDKMediaTimeEffect` (由 `TuSdKMediaTimeEffect` 替代）
* `TuSDKMediaRepeatTimeEffect` (由 `TuSdKMediaRepeatTimeEffect` 替代）
* `TuSDKMediaReversalTimeEffect` (由 `TuSdKMediaRepeatTimeEffect` 替代）
* `TuSDKMediaSlowTimeEffect` (由 `TuSdKMediaSlowTimeEffect` 替代）

## TuSdkMovieEditor

##### Added API:
* `void setVideoPath(String videoPath);`
* `void setDataSource(TuSdkMediaDataSource mediaDataSource);`
* `void setEnableTranscode(boolean isEnableTranscode)`
* `void loadVideo();`
* `void saveVideo();`
* `TuSdkEditorTranscoder getEditorTransCoder();`
* `TuSdkEditorPlayer getEditorPlayer();`
* `TuSdkEditorEffector getEditorEffector();`
* `TuSdkEditorAudioMixer getEditorMixer();`
* `TuSdkEditorSaver getEditorSaver();`


## TuSdkEditorEffector

##### Added API:
* `boolean addMediaEffectData(TuSDKMediaEffectData mediaEffectData);`
* `<T extends TuSDKMediaEffectData> List<T> mediaEffectsWithType(TuSDKMediaEffectData.TuSDKMediaEffectDataType effectType)`
* `List<TuSDKMediaEffectData> getAllMediaEffectData();`
* `void removeMediaEffectData(TuSDKMediaEffectData mediaEffectData);`
* `void removeMediaEffectsWithType(TuSDKMediaEffectData.TuSDKMediaEffectDataType mediaEffect);`
* `void removeAllMediaEffect();`


