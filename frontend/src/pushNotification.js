import { APIPrivate } from "./config/axiosconfig";

const publicVapidKey = "BDj365DrWPhw4b4qDgifZ4_OgXeuDo3WTwP3n8ogHuFa77O4uDMw-3BjDy5g1FuvXJDc7f5rdZo_HAYC8jPsQqY";

async function subscribeUser() {
    if (!('Notification' in window)) {
        return;
    }

    let register;
    try {
        register = await navigator.serviceWorker.register('/service-worker.js', {
            scope: '/'
        });
    } catch (error) {
        return;
    }

    let permission;
    try {
        permission = await Notification.requestPermission();
    } catch (error) {
        return;
    }

    if (permission !== 'granted') {
        alert('Cài đặt thông báo nếu bạn muốn nhận thông tin từ Skinly.');
        return;
    }

    let subscription;
    try {
        const existingSubscription = await register.pushManager.getSubscription();
        if (existingSubscription) {
            subscription = existingSubscription;
        } else {
            subscription = await register.pushManager.subscribe({
                userVisibleOnly: true,
                applicationServerKey: urlBase64ToUint8Array(publicVapidKey)
            });
        }

        // Chỉ gửi đến server nếu là subscription mới
      if (!existingSubscription) {
    try {
        const response = await APIPrivate.post(
            'http://localhost:8080/api/pushnotifycation/save-subscription',
            subscription,
            {
                headers: {
                    'Content-Type': 'application/json',
                },
            }
        );

        console.log('Đăng ký subscription thành công!', response.data);
    } catch (error) {
        console.error('Gửi subscription đến server thất bại:', error);
        throw error;
    }
}

    } catch (error) {
        console.log(error.message);
    }
}

function urlBase64ToUint8Array(base64String) {
    const padding = '='.repeat((4 - (base64String.length % 4)) % 4);
    const base64 = (base64String + padding).replace(/-/g, '+').replace(/_/g, '/');
    const rawData = window.atob(base64);
    const rawBytes = new Uint8Array(rawData.length);
    for (let i = 0; i < rawData.length; i++) {
        rawBytes[i] = rawData.charCodeAt(i);
    }
    return rawBytes;
}

export { subscribeUser };