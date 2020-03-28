#couldn't set gradle jdk otherwise
#Android Studio and Gradle are using different locations for the JDK.
#				Android Studio: /usr/local/java/jdk1.8.0_144
#				Gradle: /usr/local/android-studio/jre
cat gradle.properties  | sed s/#org.gradle.java/org.gradle.java/ >gradle.properties.txt
#couldn't fix this error otherwise:
# 
cat build.gradles  | sed s/\\compileOptions/compileOptions/ >gradle.properties.txt
cat build.gradles  | sed s/\\    sourceCompatibility/    sourceCompatibility/ >gradle.properties.txt
cat build.gradles  | sed s/\\    targetCompatibility/    targetCompatibility/ >gradle.properties.txt
cat build.gradles  | sed s/\\compileOptions}/}/ >gradle.properties.txt

org.koin.core.error.NoBeanDefFoundException: No definition found for 
class:'org.coepi.android.repo.SymptomRepo'. Check your definitions!
