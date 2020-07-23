# CoEpi for Android

This is the repository for the Android implementation of CoEpi. See [CoEpi.org/vision](https://www.coepi.org/vision) for background, and see the rest of our CoEpi repositories [here](https://github.com/Co-Epi). 

## Build Status
Beta branch: [![Build status](https://build.appcenter.ms/v0.1/apps/b313d675-577e-4bc4-b2db-d63532fbe872/branches/beta/badge)](https://appcenter.ms/users/danamlewis/apps/CoEpi-Android/build/branches/beta)

Develop branch: [![Build status](https://build.appcenter.ms/v0.1/apps/47ac0cf2-0d7b-478d-aa84-7ac684085222/branches/develop/badge)](https://appcenter.ms/users/scottleibrand/apps/CoEpi-Android/build/branches/develop)

## Rust core

The core functionality (domain services / networking / db) of this app was moved to a [Rust](https://www.rust-lang.org/) library, in order to share it with iOS. The repository can be found [here](https://github.com/Co-Epi/app-backend-rust). [Here](https://github.com/Co-Epi/app-android/tree/9e3d7619885da3dafc5613e2e57c15af44bebd06) you can find the previous Kotlin-only code base, if needed for whatever reason.

[More details about the architecture](https://github.com/Co-Epi/app-android/wiki/Architecture)

## Contribute


1. Read the [code guidelines](https://github.com/Co-Epi/app-android/wiki/Code-guidelines).
2. Fork or Branch
    - If you belong to the organization: Create a branch
    - If you don't belong to the organization: Fork and create a branch in the fork
3. Commit changes to the branch
4. Push your code and make a pull request
