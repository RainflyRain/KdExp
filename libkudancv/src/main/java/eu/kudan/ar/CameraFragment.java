//package eu.kudan.ar;
//
//import android.app.Fragment;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.ViewGroup;
//
//import eu.kudan.ar.camera.MyCameraManager;
//import eu.kudan.ar.camera.UIWraper;
//
///**
// * A Fragment responsible for maintaining a camera preview and both image and markerless tracking and image detection.
// */
//public class CameraFragment extends Fragment {
//
//    //region Member Variables
//
//    public UIWraper mUIWrapper = new UIWraper();
//    private MyCameraManager mCameraManager ;
//
//    public CameraFragment() {
//        super();
//    }
//    public static CameraFragment newInstance() {
//        return new CameraFragment();
//    }
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.camera_fragment, container, false);
//    }
//    @Override
//    public void onViewCreated(final View view, Bundle savedInstanceState) {
//        mCameraManager= new MyCameraManager(this,getActivity());
//        mUIWrapper.InitView(getActivity(),new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                  mCameraManager.changeTrackerMethod();
//            }
//        });
//    }
//    @Override
//    public void onPause() {
//
//        mCameraManager.teardown();
//        super.onPause();
//    }
//
//    @Override
//    public void onResume() {
//
//        super.onResume();
//        SurfaceView surfaceView = mUIWrapper.getSurfaceView();
//        mCameraManager.setup(surfaceView);
//    }
//
//    //endregion
//
//}
