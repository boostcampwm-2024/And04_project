/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const {onRequest} = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
const functions = require("firebase-functions");
const admin = require("firebase-admin");

// Firebase Admin �ʱ�ȭ
admin.initializeApp();

exports.onFriendRequestCreated = functions.firestore
    .document("USERS/{uid}/FRIEND_REQUESTS/{requestUid}")
    .onCreate(async (snapshot, context) => {
        const newRequest = snapshot.data(); // ���� ������ ���� ������
        const uid = context.params.uid; // ��û�� �޴� ����� ID
        const requestUid = context.params.requestUid; // ��û ���� ID

        // ��û ���°� RECEIVED�� ��츸 ó��
        if (newRequest.status === "RECEIVED") {
            console.log(`Friend request RECEIVED by user: ${uid}`);

            try {
                // Firestore���� ��û ���� ����� ���� ��������
                const sender = newRequest.user; // ��û ���� ����� ���� (FirestoreUser ����)
                const userDoc = await admin.firestore().collection("USERS").doc(uid).get();
                const fcmToken = userDoc.data()?.fcmToken;

                if (!fcmToken) {
                    console.log(`No FCM token found for user: ${uid}`);
                    return;
                }

                // FCM �˸� �޽��� ����
                const message = {
                    token: fcmToken,
                    notification: {
                        title: "���ο� ģ�� ��û",
                        body: `${sender.displayName}�����κ��� ģ�� ��û�� �����߽��ϴ�.`,
                    },
                    data: {
                        senderUid: sender.uid,
                        senderDisplayName: sender.displayName,
                        requestUid: requestUid,
                    },
                };

                // FCM �޽��� ����
                await admin.messaging().send(message);
                console.log(`Notification sent to user: ${uid}`);
            } catch (error) {
                console.error("Error sending notification:", error);
            }
        } else {
            console.log("Friend request not in RECEIVED state, skipping notification.");
        }
    });

