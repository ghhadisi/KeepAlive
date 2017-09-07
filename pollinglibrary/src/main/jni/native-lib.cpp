#include <jni.h>
#include <stdlib.h>

#include <string>
#include <sys/inotify.h>
#include <fcntl.h>
#include <android/log.h>
#include <malloc.h>
#include <unistd.h>

#include <sys/select.h>
#include <sys/socket.h>
#include <pthread.h>
#include <signal.h>
#include <sys/wait.h>
#include <sys/types.h>
#include <sys/un.h>
#include <errno.h>
#include <linux/signal.h>
#define LOG_TAG "tuch"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

int user_id;
extern "C"{
void start_polling(const char * service_name);
void create_child(JNIEnv *env, jstring serviceName){
    pid_t pid = fork();
    if (pid <0){
        LOGD("fork err");
    } else if (pid >0){
        LOGD("old process");
    }else {
        LOGD("new process");
        const char * service_name = ( char *) env->GetStringUTFChars(serviceName, NULL);
        start_polling(service_name);
        env->ReleaseStringChars(serviceName, (const jchar *) service_name);
    }
}
//子进程变成僵尸进程会调用这个方法
void sig_handler(int sino) {
    int status;
    //    阻塞式函数
    LOGD("等待死亡信号");
    wait(&status);
    LOGD("创建进程");
//    create_child(env,serviceName);

}

    JNIEXPORT void JNICALL
    Java_com_dbgs_keeplive_pollinglibrary_Polling_createWatcher(
            JNIEnv *env,
            jobject /* this */,jint userId, jstring serviceName) {
         user_id = userId;


        //    //为了防止子进程被弄成僵尸进程   不要    1
//    struct  sigaction sa;
//    sa.sa_flags=0;
//
//    sa.sa_handler = sig_handler;
//    sigaction(SIGCHLD, &sa, NULL);

        create_child(env,serviceName);
    }



    void* thread_run(void *data){
        pid_t  pid ;
        while ((pid = getppid()) !=1){
            sleep(2);
        }
        execlp("am", "am", "startservice", "--user", user_id,
               (char*)data, (char*)NULL);
    }
//开启线程轮询
    void start_polling(const char * service_name)
    {
        pthread_t  thread;

       int result = pthread_create(&thread, NULL, thread_run, (void *) service_name);
        LOGD("pthread_create = d%", result);
/*
 * 第一个参数为指向线程标识符的指针。
第二个参数用来设置线程属性。
第三个参数是线程运行函数的起始地址。
最后一个参数是运行函数的参数。
 * int pthread_create(pthread_t *tidp,const pthread_attr_t *attr,
 * 若线程创建成功，则返回0。若线程创建失败，则返回出错编号，并且*thread中的内容是未定义的。
 */
    }


}