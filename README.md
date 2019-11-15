# Radar Card ISG

[English](https://github.com/ksavas/RadarCardISG/blob/master/README.en.md)

Şirketlerin çeşitli yerlerde kağıt üzerindeki onay kutularını işaretleyerek yaptıkları teftiş işlemlerinin, kağıt kullanılmadan telefon üzerinden kullanılmaya çalışılan bir mobil uygulama.

Uygulama kapsamında,

Uygulamayı kullanacak şirket personeli kendi kullanıcı adı ve şifresiyle uygulamaya giriş yapacak, giriş yaptıktan sonra bir görevler listesiyle karşılaşacak bu görevler listesinden uygun olan görevi seçip teftiş işlemine başlayacak.

Teftiş edilen bölgeler switch buttonuyla tamam veya hayır şeklinde kaydedilecek. Teftiş edilip onay alamayan bölgelerdeki eksiklikler not edilerek kaydedilecek ve isteğe bağlı olarak teftiş edilen bölgelerin fotoğrafları çekilebilecek.

## Teknolojiler

Uygulama, Android Studio ile java programlama dili kullanılarak geliştirildi. Bazı operasyonlar için 3rd Party yazılımlardan destek alındı.

UI tarafı, başlangıçta xml olarak düzenleniyordu. Daha sonra uygulamanın dinamizmi açısından gerekli görüldüğü için bütün tasarım java programlama dili, android UI kütüphanelerinden faydalanılarak baştan düzenlendi.

### Uygulamadan Görüntüler

| MainActivity.java | TaskProviderActivity.java | TaskActivity.java |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/ksavas/RadarCardISG/master/p1.png"> | <img src="https://raw.githubusercontent.com/ksavas/RadarCardISG/master/p2.png"> | <img src="https://raw.githubusercontent.com/ksavas/RadarCardISG/master/p3.png"> |


