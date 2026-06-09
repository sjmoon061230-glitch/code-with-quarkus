function validateAndUpdate() {
let valid = true;
const email = document.getElementById('updateEmail').value.trim();
const phone = document.getElementById('updatePhone').value.trim();
// ① 이메일 형식 검사
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
if (!emailRegex.test(email)) {
showFieldError('updateEmail', 'updateEmailMsg',
'올바른 이메일 형식이 아닙니다.');
valid = false;
} else {
clearFieldError('updateEmail');
}
// ② 연락처 형식 검사
const phoneRegex = /^010-\d{4}-\d{4}$/;
if (!phoneRegex.test(phone)) {
showFieldError('updatePhone', 'updatePhoneMsg',
'010-0000-0000 형식으로 입력해주세요.');
valid = false;
} else {
clearFieldError('updatePhone');
}
if (valid) document.getElementById('updateForm').submit();
}
// profile.js 전용 showError / clearError
function showFieldError(fieldId, msgId, message) {
const field = document.getElementById(fieldId);
field.classList.add('is-invalid');
const msg = document.getElementById(msgId);
if (msg) msg.textContent = message;
}
function clearFieldError(fieldId) {
const field = document.getElementById(fieldId);
field.classList.remove('is-invalid');
field.classList.add('is-valid');
}

window.onload = function() {
// 기존 fetch 코드 전체 유지
// URL 파라미터 오류 감지
const params = new URLSearchParams(window.location.search);
const error = params.get('error');
const success = params.get('success');
const msgEl = document.getElementById('updateMsg');
if (success === 'updated') {
msgEl.className = 'alert alert-success';
msgEl.textContent = ' 개인정보가 수정되었습니다.';
} else if (error === 'duplicate_email') {
msgEl.className = 'alert alert-danger';
msgEl.textContent = ' 이미 사용 중인 이메일입니다.';
}
if (error === 'wrong_password') {
// ① Toast 먼저 (즉각 알림)
showToast(' 현재 비밀번호가 일치하지 않습니다.', 'danger');
const pwMsgEl = document.getElementById('pwMsg');
if (pwMsgEl) {
pwMsgEl.className = 'alert alert-danger';
pwMsgEl.textContent = ' 현재 비밀번호가 일치하지 않습니다.';
}
}
if (error) {
const messages = {
'invalid_type': 'jpg, png, gif, webp 파일만 가능합니다.',
'too_large': '파일 크기는 5MB 이하여야 합니다.',
'upload_fail': '업로드 실패. 다시 시도해주세요.'
};
const msg = messages[error];
const div = document.getElementById('uploadErrorMsg');
if (msg && div) {
div.textContent = msg;
div.classList.remove('d-none');
}
}
}

// 비밀번호 변경 성공 처리, window.onload 안에 삽입
if (success === 'password_changed') {
// Toast 출력
showToast(
' 비밀번호가 변경 완료, 로그인 페이지로 이동합니다.',
'success'
);
// 3.5초 후 로그인 페이지로 이동
setTimeout(function() {
window.location.href = '/logout?next=login';
}, 3500);
}