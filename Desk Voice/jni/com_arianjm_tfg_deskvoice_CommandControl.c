/*
 * com_arianjm_tfg_deskvoice_CommandControl.c
 *
 *  Created on: 20/06/2013
 *      Author: Arián
 */

#include <windows.h>
#include <stdio.h>
#include "com_arianjm_tfg_deskvoice_CommandControl.h"

JNIEXPORT void JNICALL Java_com_arianjm_tfg_deskvoice_CommandControl_multimediaKey (JNIEnv *env, jclass thisClass, jint key){

	//Key press
	keybd_event( key, 0x45, KEYEVENTF_EXTENDEDKEY | 0, 0 );
	//Key release
	keybd_event( key, 0x45, KEYEVENTF_EXTENDEDKEY | KEYEVENTF_KEYUP, 0);

	return;
}

JNIEXPORT void JNICALL Java_com_arianjm_tfg_deskvoice_CommandControl_press  (JNIEnv *env, jclass thisClass, jint key){
	//Key press
	keybd_event( key, 0x45, KEYEVENTF_EXTENDEDKEY | 0, 0 );
	return;
}

JNIEXPORT void JNICALL Java_com_arianjm_tfg_deskvoice_CommandControl_release (JNIEnv *env, jclass thisClass, jint key){
	//Key release
	keybd_event( key, 0x45, KEYEVENTF_EXTENDEDKEY | KEYEVENTF_KEYUP, 0);
	return;
}
