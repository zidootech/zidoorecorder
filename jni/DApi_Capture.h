#ifndef __DAPI_CAPTURE_H_
#define __DAPI_CAPTURE_H_

#include "MsTypes.h"
#include <media/mediarecorder.h>

#include "appdemo_capture_types.h"




#ifdef __cplusplus
extern "C" {
#endif
using namespace android;

class LiveCapture 
{
public:
	LiveCapture(sp<MediaRecorderListener> listener);
	~LiveCapture();
    bool DApi_Capture_start(void);
    bool DApi_Capture_stop(void);
    void Set_EnVideo_Bitrate(int bitrate);
    void Set_EnVideo_Source(CAPTURE_SOURCE source_type);
    void Set_EnVideo_cameraid(int cameraid);
    void Set_Video_Wigth(int video_wight);
    void Set_Video_High(int video_hight);
    void Set_Video_Framerate(int video_framerate);
    void Set_File_Path(int fd);
    void Set_RecAuido(bool);
	void Set_Video_TravelingMode(EN_DAPI_CAPTURE_SOURCE_TYPE travelingMode);
	void Set_Video_SubSource(int sub);
	void Set_Video_OutputFormat(int format);
    bool Open_File();
    bool Close_File();
private:
    int mCameraid ;
    int mVideoWigth;   							 //video wigth  320,480,640,1280
    int mVideoHigh;    							 //video high    240,320,480,720
    int mBitrate; 							 	 //  bit    < 2000 suggest
    int mVideoFramerate ;					     //20
    int mFormat;
    bool bRecAuido;
    EN_DAPI_CAPTURE_VIDEO_TYPE mEnVideoType;
    EN_DAPI_CAPTURE_SOURCE_TYPE mEnTravelingMode; //1:allwithosd  2:allwithoutosd  4:sub  6: main
    int mSubSource;
	sp<MediaRecorderListener> mMediaRecorderListener;

    
    const char *mFileName ;
    int mFilePoint ;
    sp<Camera>        mLivecapCamera ;
    sp<MediaRecorder> mLivecapMediaRecorder ;
};

#ifdef __cplusplus
}
#endif
#endif

