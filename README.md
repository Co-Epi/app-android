# CoEpi for Android

This is the repository for the Android implementation of CoEpi. See [CoEpi.org/vision](https://www.coepi.org/vision) for background, and see the rest of our CoEpi repositories [here](https://github.com/Co-Epi). 

## Build Status
Beta branch: [![Build status](https://build.appcenter.ms/v0.1/apps/b313d675-577e-4bc4-b2db-d63532fbe872/branches/beta/badge)](https://appcenter.ms/users/danamlewis/apps/CoEpi-Android/build/branches/beta)

Develop branch: [![Build status](https://build.appcenter.ms/v0.1/apps/47ac0cf2-0d7b-478d-aa84-7ac684085222/branches/develop/badge)](https://appcenter.ms/users/scottleibrand/apps/CoEpi-Android/build/branches/develop)

## Rust core

The core functionality (domain services / networking / db) of this app was moved to a [Rust](https://www.rust-lang.org/) library, in order to share it with iOS. The repository can be found [here](https://github.com/Co-Epi/app-backend-rust). [Here](https://github.com/Co-Epi/app-android/tree/9e3d7619885da3dafc5613e2e57c15af44bebd06) you can find the previous Kotlin-only code base, if needed for whatever reason.

[More details about the architecture](https://github.com/Co-Epi/app-android/wiki/Architecture)

## Contribute

CoEpi is an open source project with an MIT license - please do feel free to contribute improvements!

1. Some [code guidelines](https://github.com/Co-Epi/app-android/wiki/Code-guidelines) and recommendations exist.
2. For new contributors, fork the `develop` branch to get started.
3. Commit changes to your version branch. 
4. Push your code, and make a pull request back to the CoEpi `develop` branch. 

Need help getting started? Just ask! You can [open an issue](https://github.com/Co-Epi/app-android/issues/new/choose), or start your PR, and tag `@danamlewis` or `@scottleibrand` in a comment to ask for assistance.

## Test
* If you're not a developer, try using the download link [here](https://bit.ly/CoEpiAndroidbeta) to get a version of the app to test on your phone, without needing to sign in anywhere. 
* `Note: Because you are not getting the app through the app store, Android will warn you that this is an untrusted app. There is no information gathered by the CoEpi app; no username, email, or any other information gathered. If you like, you can review all the code in this repository as part of your evaluation to trust the app on your phone.` 
