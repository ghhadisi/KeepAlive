#include <jni.h>
#include <stdlib.h>

#include <string>
#include <sys/inotify.h>
#include <fcntl.h>
#include <android/log.h>
#include <malloc.h>
#include <unistd.h>
#define LOG_TAG "tuch"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
extern "C"{
    JNIEXPORT void JNICALL
    Java_com_dbgs_uninstallfeedbacklibrary_UninstallFeedbackUtil_load(
            JNIEnv *env,
            jobject /* this */,jint sdk, jstring  path) {
        pid_t pid = fork();
        if (pid <0){
            LOGD("fork err");
        } else if (pid >0){
            LOGD("old process");

        }else {
            LOGD("new process");
            const char *pPath = (char *) env->GetStringUTFChars(path, 0);

            //**************** 可忽略****************************************
            int observer = open(pPath, O_RDONLY);
            if (observer == -1) {
//            创建 文件  observedFile    线程  1   2 轮询  observerFile
                observer = open(pPath, O_CREAT);
            }
            //******************* 可忽略*************************************

            //    初始化  inotify linux  可以监听文件的状态
            int fileDescriple = inotify_init();
            int watch = inotify_add_watch(fileDescriple, pPath, IN_DELETE_SELF);//IN_ALL_EVENTS
            if (watch < 0) {
                LOGD("监听失败");
                exit(0);
            }
            void *p_buf = malloc(sizeof(struct inotify_event));
            //        阻塞式函数  anr
            size_t readBytes = read(fileDescriple, p_buf, sizeof(struct inotify_event));

//            执行到下面来了    文件发生了改变  用户卸载了 app
            LOGD("监听类型 %d    ,%d",((struct inotify_event *) p_buf)->mask,IN_DELETE_SELF);
//            inotify_rm_watch(fileDescriple, watch);
            if (((struct inotify_event *) p_buf)->mask == IN_DELETE_SELF) {
//////                覆盖安装
                FILE *app_file = fopen(pPath, "r");
                if (app_file == NULL) {
                    //   删除app   移除监听
                    inotify_rm_watch(fileDescriple, watch);
                } else{
                    //   覆盖安装    重新进行监听
                    fclose(app_file);
                    FILE *p_observelFile = fopen(pPath, "w");
                    int watchDescip=inotify_add_watch(fileDescriple, pPath, IN_DELETE_SELF);
                }
            }
//        LOGD("跳转网页");
////        铁了心要删除app  sdk   17   am  设置到环境变量   多用户的操作
            if (sdk < 17) {
                execlp("am", "am", "start", "-a", "android.intent.action.VIEW", "-d",
                       "http://www.baidu.com",NULL);

            } else{
                execlp("am", "am", "start","--user","0","-a", "android.intent.action.VIEW", "-d",
                       "http://www.baidu.com",NULL);
            }

            free(p_buf);
            env->ReleaseStringChars(path, (const jchar *) pPath);

        }
    }
}