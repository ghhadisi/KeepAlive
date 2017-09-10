#include <jni.h>
#include <string>
#include "native-lib.h"


char * user_id;
int m_child;
int m_parent = -1;

extern "C" {

    void child_listen_msg() {
        fd_set rfds;
        while (1) {
            //清空端口号
            FD_ZERO(&rfds);
            FD_SET(m_child,&rfds);

        }
    }
    void child_do_work() {
    //    守护进程
    //   1 建立socket服务
    //    2读取消息
        if(child_create_channel()) {
            child_listen_msg();
        }

    }

    void create_child(){
        pid_t pid = fork();
        if (pid <0){
            LOGE("fork err");
        } else if (pid >0){
            LOGE("old process");
        }else {
            LOGE("子进程开启 ");
    //       守护进程
            child_do_work();

        }
    }

    JNIEXPORT void JNICALL Java_com_dbgs_keepalive_socketlibrary_Watcher_createWatcher(JNIEnv *env, jobject instance,
                jstring userId) {

        // TODO
        user_id= (char *) env->GetStringUTFChars(userId, NULL);
        create_child();


        }

    JNIEXPORT void JNICALL
    Java_com_dbgs_keepalive_socketlibrary_Watcher_connectToMonitor(JNIEnv *env, jobject instance) {

        // TODO

    }
}