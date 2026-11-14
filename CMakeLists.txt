# Builds the track editor as one jar with everything it needs, trackeditor.jar,
# next to this file: run it with java -jar trackeditor.jar. Nothing is installed.
#   cmake -S . -B build && cmake --build build

cmake_minimum_required(VERSION 3.10)

project(trackeditor NONE)

find_package(Java 1.8 REQUIRED COMPONENTS Development)
include(UseJava)

set(_JAVA_SRC
      gui/CheckDialog.java
      gui/DefaultObjectsDialog.java
      gui/DefaultSurfacesDialog.java
      gui/DeltaPanel.java
      gui/EditorFrame.java
      gui/MenuScroller.java
      gui/NewProjectDialog.java
      gui/PreferencesDialog.java
      gui/TrackgenPanel.java
      gui/TrackMapImage.java
      gui/TrackObjectDialog.java
      gui/properties/CameraProperties.java
      gui/properties/EnvMapProperties.java
      gui/properties/GeneralProperties.java
      gui/properties/GeoreferenceProperties.java
      gui/properties/GraphicObjectData.java
      gui/properties/GraphicObjectProperties.java
      gui/properties/GraphicProperties.java
      gui/properties/Graphic3DProperties.java
      gui/properties/ImageProperties.java
      gui/properties/LocalInfoProperties.java
      gui/properties/ObjectMapProperties.java
      gui/properties/ObjectProperties.java
      gui/properties/PitProperties.java
      gui/properties/PropertiesDialog.java
      gui/properties/PropertyPanel.java
      gui/properties/ReliefProperties.java
      gui/properties/SectorProperties.java
      gui/properties/SegmentChooser.java
      gui/properties/StartingGridProperties.java
      gui/properties/SurfaceProperties.java
      gui/properties/TerrainProperties.java
      gui/properties/TrackLightProperties.java
      gui/properties/TrackProperties.java
      gui/properties/TurnMarksProperties.java
      gui/segment/GroupButton.java
      gui/segment/ProfileButton.java
      gui/segment/SegmentEditorDlg.java
      gui/segment/SegmentSideProperties.java
      gui/segment/SegmentSlider.java
      gui/segment/SliderListener.java
      gui/splash/SplashPanel.java
      gui/splash/SplashScreen.java
      gui/splash/StatusBar.java
      gui/view/CircuitView.java
      gui/view/CircuitViewSelectionEvent.java
      gui/view/CircuitViewSelectionListener.java
      gui/view/SegmentSplitter.java
      gui/view3d/CameraPlayer.java
      gui/view3d/OrbitCamera.java
      gui/view3d/RaceLine.java
      gui/view3d/RoadMesh.java
      gui/view3d/SceneCsrSd.java
      gui/view3d/SceneTorcs.java
      gui/view3d/Track3DRenderer.java
      gui/view3d/Track3DWindow.java
      gui/view3d/TrackCrossing.java
      gui/view3d/TrackScene.java
      miscel/EPMath.java
      miscel/TCPopupEventQueue.java
      plugin/torcs/GfParmNumbers.java
      plugin/torcs/NoOpEntityResolver.java
      plugin/torcs/XmlReader.java
      plugin/torcs/XmlWriter.java
      utils/AppName.java
      utils/AtomicFile.java
      utils/FileProblem.java
      utils/CustomFileFilter.java
      utils/DoubleValue.java
      utils/EditorPoint.java
      utils/GameType.java
      utils/GroupButtonLayout.java
      utils/MutableDouble.java
      utils/MutableInteger.java
      utils/MutableString.java
      utils/Project.java
      utils/ProjectFile.java
      utils/Properties.java
      utils/SegmentSliderLayout.java
      utils/SegmentVector.java
      utils/SgiReader.java
      utils/SurfaceComboBox.java
      utils/TrackData.java
      utils/check/CameraProblems.java
      utils/check/CheckMessage.java
      utils/check/Closure.java
      utils/check/ImportGuard.java
      utils/check/LoadedTrack.java
      utils/check/ReadNotices.java
      utils/check/Subject.java
      utils/check/TrackCheck.java
      utils/check/TrackChecker.java
      utils/check/TrackContext.java
      utils/check/TrackFolders.java
      utils/check/TrackLoader.java
      utils/check/Trackgen.java
      utils/check/TrackgenOutput.java
      utils/TrackEditor.java
      utils/ac3d/Ac3d.java
      utils/ac3d/Ac3dException.java
      utils/ac3d/Ac3dMaterial.java
      utils/ac3d/Ac3dObject.java
      utils/ac3d/Ac3dSurface.java
      utils/circuit/Camera.java
      utils/circuit/Curve.java
      utils/circuit/EnvironmentMapping.java
      utils/circuit/GameSides.java
      utils/circuit/GameTrack.java
      utils/circuit/Georeference.java
      utils/circuit/Graphic.java
      utils/circuit/GraphicObject.java
      utils/circuit/Header.java
      utils/circuit/LocalInfo.java
      utils/circuit/MainTrack.java
      utils/circuit/ObjShapeHandle.java
      utils/circuit/ObjShapeObject.java
      utils/circuit/ObjShapeRelief.java
      utils/circuit/ObjShapeTerrain.java
      utils/circuit/ObjectData.java
      utils/circuit/ObjectMap.java
      utils/circuit/Pits.java
      utils/circuit/Point3D.java
      utils/circuit/Reliefs.java
      utils/circuit/RoadTexture.java
      utils/circuit/Sector.java
      utils/circuit/Segment.java
      utils/circuit/SegmentListener.java
      utils/circuit/SegmentSide.java
      utils/circuit/SegmentSideListener.java
      utils/circuit/StartingGrid.java
      utils/circuit/Straight.java
      utils/circuit/Surface.java
      utils/circuit/TerrainGeneration.java
      utils/circuit/TrackLight.java
      utils/circuit/TrackObject.java
      utils/circuit/TurnMarks.java
      utils/circuit/Version3.java
      utils/circuit/XmlObjPits.java
      utils/undo/ObjectMapObject.java
      utils/undo/DeletedObject.java
      utils/undo/MovedObject.java
      utils/undo/NameCounters.java
      utils/undo/Snapshot.java
      utils/undo/Undo.java
      utils/undo/UndoAddGraphicObject.java
      utils/undo/UndoAddObject.java
      utils/undo/UndoAddSegment.java
      utils/undo/UndoCameras.java
      utils/undo/UndoDeleteAllGraphicObjects.java
      utils/undo/UndoDeleteGraphicObject.java
      utils/undo/UndoDeleteObject.java
      utils/undo/UndoDeleteRelief.java
      utils/undo/UndoDeleteAllObjects.java
      utils/undo/UndoDeleteSegment.java
      utils/undo/UndoEditAllObjects.java
      utils/undo/UndoEditGraphicObject.java
      utils/undo/UndoEditObject.java
      utils/undo/UndoEditRelief.java
      utils/undo/UndoInterface.java
      utils/undo/UndoMoveAllGraphicObjects.java
      utils/undo/UndoMoveGraphicObject.java
      utils/undo/UndoPitGeneration.java
      utils/undo/UndoProperties.java
      utils/undo/UndoSegmentChange.java
      utils/undo/UndoSplitSegment.java)

set(_PNGS gui/splash/Splash.png icon.png)

set(_ICONS gui/icons/Calc24.gif
         gui/icons/Camera24.gif
         gui/icons/Check24.gif
         gui/icons/Cut24.gif
         gui/icons/Export24.gif
         gui/icons/FindAgain24.gif
         gui/icons/Finish24.gif
         gui/icons/Help24.gif
         gui/icons/Movie24.gif
         gui/icons/New24.gif
         gui/icons/Object24.gif
         gui/icons/Open24.gif
         gui/icons/Properties24.gif
         gui/icons/Redo24.gif
         gui/icons/Relief24.gif
         gui/icons/Save24.gif
         gui/icons/Search24.gif
         gui/icons/Straight24.gif
         gui/icons/Subdivide24.gif
         gui/icons/TurnLeft24.gif
         gui/icons/TurnRight24.gif
         gui/icons/Undo24.gif
         gui/icons/View3D24.gif
         gui/icons/Zoom24.gif
         gui/icons/ZoomIn24.gif
         gui/icons/ZoomOut24.gif
)
set(_JAVA_JARS bsh-2.0b4.jar
               jdom-2.0.6.1.jar
               jgoodies-looks-2.7.0.jar
               jgoodies-common-1.8.1.jar
               gluegen-rt.jar
               jogl-all.jar)

# JOGL's native libraries (3D view): JOGL loads them from these jars, so the
# ones present go into trackeditor.jar; a platform without them has no 3D view
set(_JOGL_NATIVE_JARS)
foreach(_PLATFORM windows-amd64 linux-amd64 linux-aarch64 macosx-universal)
   set(_MISSING)
   foreach(_JAR gluegen-rt-natives-${_PLATFORM}.jar jogl-all-natives-${_PLATFORM}.jar)
      if(EXISTS "${CMAKE_CURRENT_SOURCE_DIR}/${_JAR}")
         list(APPEND _JOGL_NATIVE_JARS ${_JAR})
      else()
         list(APPEND _MISSING ${_JAR})
      endif()
   endforeach()
   if(_MISSING)
      message(WARNING "trackeditor.jar will have no 3D view on ${_PLATFORM}: missing ${_MISSING}")
   endif()
endforeach()

# Target Java 8: --release catches accidental use of post-8 APIs at
# compile time; -source/-target is the fallback when building with JDK 8
if(Java_VERSION_MAJOR VERSION_GREATER_EQUAL "9")
   set(CMAKE_JAVA_COMPILE_FLAGS "--release" "8")
else()
   set(CMAKE_JAVA_COMPILE_FLAGS "-source" "1.8" "-target" "1.8")
endif()

# The editor's own classes and resources, in the build folder: only a step
# towards trackeditor.jar
add_jar(trackeditor-classes
        SOURCES ${_JAVA_SRC} ${_PNGS} ${_ICONS}
        INCLUDE_JARS ${_JAVA_JARS}
        OUTPUT_DIR "${CMAKE_CURRENT_BINARY_DIR}")
get_target_property(_CLASSES_JAR trackeditor-classes JAR_FILE)

# The track checker (java -jar trackeditor.jar check) runs without a window: fail
# the build when what it uses reaches the editor's window code (see
# utils/check/ImportGuard.java)
add_custom_command(TARGET trackeditor-classes POST_BUILD
                   COMMAND ${Java_JAVA_EXECUTABLE} -cp "${_CLASSES_JAR}" utils.check.ImportGuard
                   COMMENT "Checking the track checker uses none of the editor's window code"
                   VERBATIM)

# ========= trackeditor.jar: the editor's classes and all the jars it uses

set(_JAR_FILE "${CMAKE_CURRENT_SOURCE_DIR}/trackeditor.jar")
set(_STAGING "${CMAKE_CURRENT_BINARY_DIR}/trackeditor-staging")

# Its manifest: everything is in the jar, so there is no Class-Path
set(_MANIFEST "${CMAKE_CURRENT_BINARY_DIR}/trackeditor-MANIFEST.MF")
file(WRITE "${_MANIFEST}"
"Manifest-Version: 1.0
Main-Class: utils.TrackEditor
Enable-Native-Access: ALL-UNNAMED
Add-Opens: java.desktop/sun.awt java.desktop/sun.awt.win
 dows java.desktop/sun.java2d
")

# The jars it is made of, for the assembly script
set(_DEPENDENCIES)
set(_DEPENDENCY_LIST "")
foreach(_JAR ${_JAVA_JARS} ${_JOGL_NATIVE_JARS})
   list(APPEND _DEPENDENCIES "${CMAKE_CURRENT_SOURCE_DIR}/${_JAR}")
   set(_DEPENDENCY_LIST "${_DEPENDENCY_LIST}${CMAKE_CURRENT_SOURCE_DIR}/${_JAR}\n")
endforeach()
set(_DEPENDENCY_FILE "${CMAKE_CURRENT_BINARY_DIR}/trackeditor-jars.txt")
file(WRITE "${_DEPENDENCY_FILE}" "${_DEPENDENCY_LIST}")

# The paths, in a file the assembly script reads, avoiding command-line quoting
# issues with MSBuild on Windows
set(_CONFIG "${CMAKE_CURRENT_BINARY_DIR}/trackeditor-config.cmake")
file(WRITE "${_CONFIG}"
"set(JAR_EXE \"${Java_JAR_EXECUTABLE}\")
set(STAGING \"${_STAGING}\")
set(CLASSES_JAR \"${_CLASSES_JAR}\")
set(DEPENDENCY_FILE \"${_DEPENDENCY_FILE}\")
set(JAR_FILE \"${_JAR_FILE}\")
set(MANIFEST \"${_MANIFEST}\")
")

# The assembly script: extracts all the jars, removes their signatures, and
# packs them again as one
set(_SCRIPT "${CMAKE_CURRENT_BINARY_DIR}/assemble-trackeditor.cmake")
file(WRITE "${_SCRIPT}"
"include(\${CMAKE_CURRENT_LIST_DIR}/trackeditor-config.cmake)

file(REMOVE_RECURSE \"\${STAGING}\")
file(MAKE_DIRECTORY \"\${STAGING}\")

file(STRINGS \"\${DEPENDENCY_FILE}\" _JARS)
foreach(_JAR \${_JARS})
   execute_process(COMMAND \"\${JAR_EXE}\" xf \"\${_JAR}\"
                   WORKING_DIRECTORY \"\${STAGING}\"
                   RESULT_VARIABLE _RC)
   if(_RC)
      message(FATAL_ERROR \"Failed to extract \${_JAR}\")
   endif()
endforeach()

# the editor's classes last, so its classes and resources take priority
execute_process(COMMAND \"\${JAR_EXE}\" xf \"\${CLASSES_JAR}\"
                WORKING_DIRECTORY \"\${STAGING}\"
                RESULT_VARIABLE _RC)
if(_RC)
   message(FATAL_ERROR \"Failed to extract \${CLASSES_JAR}\")
endif()

# the jars' signatures aren't valid once they are packed again
file(GLOB _SIG_FILES \"\${STAGING}/META-INF/*.SF\"
                     \"\${STAGING}/META-INF/*.DSA\"
                     \"\${STAGING}/META-INF/*.RSA\"
                     \"\${STAGING}/META-INF/*.EC\")
if(_SIG_FILES)
   file(REMOVE \${_SIG_FILES})
endif()

execute_process(COMMAND \"\${JAR_EXE}\" cfm \"\${JAR_FILE}\" \"\${MANIFEST}\" .
                WORKING_DIRECTORY \"\${STAGING}\"
                RESULT_VARIABLE _RC)
if(_RC)
   message(FATAL_ERROR \"Failed to create \${JAR_FILE}\")
endif()
")

add_custom_command(OUTPUT "${_JAR_FILE}"
                   # the jar file too, not only the target, so a change to the classes packs it again
                   DEPENDS trackeditor-classes "${_CLASSES_JAR}" ${_DEPENDENCIES}
                   COMMAND "${CMAKE_COMMAND}" -P "${_SCRIPT}"
                   COMMENT "Packaging trackeditor.jar")

add_custom_target(trackeditor ALL DEPENDS "${_JAR_FILE}")

# ========= end trackeditor.jar

# Makes the sources available in a generated IDE project
add_custom_target(trackeditor-src SOURCES ${_JAVA_SRC} ${_PNGS} ${_ICONS})

# Where the jars come from:
# https://github.com/beanshell/beanshell/releases
# http://www.jdom.org/downloads/ (jdom-2.0.6.1.zip: only jdom-2.0.6.1.jar is needed)
# http://www.java2s.com/Code/JarDownload/jgoodies/jgoodies-common-1.8.1.jar.zip
# https://repo1.maven.org/maven2/com/jgoodies/jgoodies-looks/2.7.0/jgoodies-looks-2.7.0.jar
# https://jogamp.org/deployment/v2.6.0/jar/ (gluegen-rt, jogl-all and their -natives- jars)
