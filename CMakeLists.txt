# If the OPTION_TRACKEDITOR exists, then this is being built as part of
# the Speed-Dreams build. To allow building the trackeditor without
# building all of SD, set the _STANDALONE flag which is used to skip 
# the SD specific calls

cmake_minimum_required(VERSION 3.10)

IF(POLICY CMP0048)
   CMAKE_POLICY(SET CMP0048 NEW)
ENDIF(POLICY CMP0048)

IF(OPTION_TRACKEDITOR)
   INCLUDE(../../../cmake/macros.cmake)
   SET(_STANDALONE False)
ELSE(OPTION_TRACKEDITOR)
   SET(_STANDALONE True)
   SET(CSR_TOOLS_EXECPREFIX csr-)
   SET(CSR_BINDIR bin)
ENDIF(OPTION_TRACKEDITOR)

FIND_PACKAGE(Java 1.8 COMPONENTS Development)
IF(Java_Development_FOUND AND Java_FOUND)
   INCLUDE(UseJava)


   PROJECT(trackeditor NONE)

   SET(_TARGET_NAME ${CSR_TOOLS_EXECPREFIX}trackeditor)

   SET(_JAVA_SRC
         gui/CheckDialog.java
         gui/DefaultObjectsDialog.java
         gui/DefaultSurfacesDialog.java
         gui/DeltaPanel.java
         gui/EditorFrame.java
         gui/NewProjectDialog.java
         gui/PreferencesDialog.java
         gui/TrackgenPanel.java
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
         gui/view3d/GameTrack.java
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
         plugin/torcs/XMLOutput.java
         plugin/torcs/XmlReader.java
         plugin/torcs/XmlWriter.java
         utils/AtomicFile.java
         utils/CustomFileFilter.java
         utils/DoubleValue.java
         utils/EditorPoint.java
         utils/GroupButtonLayout.java
         utils/MutableDouble.java
         utils/MutableInteger.java
         utils/MutableString.java
         utils/Project.java
         utils/Properties.java
         utils/SegmentSliderLayout.java
         utils/SegmentVector.java
         utils/SurfaceComboBox.java
         utils/TrackData.java
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
         utils/undo/Undo.java
         utils/undo/UndoAddGraphicObject.java
         utils/undo/UndoAddObject.java
         utils/undo/UndoAddSegment.java
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
         utils/undo/UndoSegmentChange.java
         utils/undo/UndoSplitSegment.java)

   #file(GLOB_RECURSE _JAVA_SRC ./*.java)
   #foreach(_jfile ${_JAVA_SRC})
   #   message(STATUS "${_jfile}")
   #endforeach()

   SET(_PNGS gui/splash/Splash.png icon.png)
   #file(GLOB_RECURSE _PNGS *.png)

   SET(_ICONS gui/icons/Calc24.gif
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

   SET(JAVA_JARS bsh-2.0b4.jar
                  jdom-1.1.3.jar
                  jgoodies-looks-2.5.3.jar
                  jgoodies-common-1.8.1.jar
                  gluegen-rt.jar
                  jogl-all.jar )

   # JOGL's native libraries (3D View): JOGL loads them from these jars when they
   # are beside gluegen-rt.jar and jogl-all.jar. Only the ones present are used.
   SET(_JOGL_NATIVE_JARS)
   FOREACH(_PLATFORM windows-amd64 linux-amd64 linux-aarch64 macosx-universal)
      FOREACH(_JAR gluegen-rt-natives-${_PLATFORM}.jar jogl-all-natives-${_PLATFORM}.jar)
         IF(EXISTS "${CMAKE_CURRENT_LIST_DIR}/${_JAR}")
            LIST(APPEND _JOGL_NATIVE_JARS ${_JAR})
         ENDIF()
      ENDFOREACH()
   ENDFOREACH()

   # ========= Debug
   #message(STATUS "JAVA_JARS = ${JAVA_JARS}")
   #message(STATUS "_JAVA_SRC = ${_JAVA_SRC}")
   #message(STATUS "_PNGS = ${_PNGS}")
   #message(STATUS "_ICONS = ${_ICONS}")
   # ========= Debug

   # Target Java 8: --release catches accidental use of post-8 APIs at
   # compile time; -source/-target is the fallback when building with JDK 8
   IF(Java_VERSION_MAJOR VERSION_GREATER_EQUAL "9")
      SET(CMAKE_JAVA_COMPILE_FLAGS "--release" "8")
   ELSE()
      SET(CMAKE_JAVA_COMPILE_FLAGS "-source" "1.8" "-target" "1.8")
   ENDIF()

   SET(_TGT_DIR "${CMAKE_BINARY_DIR}/${CSR_BINDIR}")

   ADD_JAR(${_TARGET_NAME} 
            SOURCES ${_JAVA_SRC} gui/splash/Splash.png icon.png ${_ICONS} META-INF/MANIFEST.MF
            INCLUDE_JARS ${JAVA_JARS}
            MANIFEST META-INF/MANIFEST.MF
            OUTPUT_DIR ${_TGT_DIR}
            )

   # Copy each jar dependency to the bin folder (for running without installing).
      ADD_CUSTOM_COMMAND(TARGET ${_TARGET_NAME} POST_BUILD
                        COMMAND ${CMAKE_COMMAND} -E make_directory "${_TGT_DIR}"
                        VERBATIM)
      FOREACH(_JAR ${JAVA_JARS} ${_JOGL_NATIVE_JARS})
         ADD_CUSTOM_COMMAND(TARGET ${_TARGET_NAME} POST_BUILD
                              COMMAND ${CMAKE_COMMAND} -E echo Copying "${_JAR}" to "${_TGT_DIR}"
                              COMMAND ${CMAKE_COMMAND} -E copy "${CMAKE_CURRENT_LIST_DIR}/${_JAR}" "${_TGT_DIR}"
                              VERBATIM)
      ENDFOREACH()


   # ========= Fat JAR: single self-contained jar with all dependencies
   # Run with: java -jar <name>-fat.jar

   get_target_property(_MAIN_JAR ${_TARGET_NAME} JAR_FILE)
   SET(_FAT_JAR_NAME "${_TARGET_NAME}-fat.jar")
   SET(_FAT_JAR "${_TGT_DIR}/${_FAT_JAR_NAME}")
   SET(_FAT_STAGING "${CMAKE_BINARY_DIR}/fat-jar-staging")

   # Fat jar manifest: same as the main jar but without Class-Path
   SET(_FAT_MANIFEST "${CMAKE_BINARY_DIR}/fat-jar-MANIFEST.MF")
   FILE(WRITE "${_FAT_MANIFEST}"
"Manifest-Version: 1.0
Main-Class: utils.TrackEditor
Enable-Native-Access: ALL-UNNAMED
Add-Opens: java.desktop/sun.awt java.desktop/sun.awt.win
 dows java.desktop/sun.java2d
")

   # Write the list of dependency jars for the assembly script
   SET(_FAT_JARS_LIST "${CMAKE_BINARY_DIR}/fat-jar-deps.txt")
   SET(_FAT_JARS_CONTENT "")
   FOREACH(_JAR ${JAVA_JARS} ${_JOGL_NATIVE_JARS})
      SET(_FAT_JARS_CONTENT "${_FAT_JARS_CONTENT}${CMAKE_CURRENT_LIST_DIR}/${_JAR}\n")
   ENDFOREACH()
   FILE(WRITE "${_FAT_JARS_LIST}" "${_FAT_JARS_CONTENT}")

   # Write all paths into a config file read by the assembly script,
   # avoiding command-line quoting issues with MSBuild on Windows
   SET(_FAT_CONFIG "${CMAKE_BINARY_DIR}/fat-jar-config.cmake")
   FILE(WRITE "${_FAT_CONFIG}"
"set(JAR_EXE \"${Java_JAR_EXECUTABLE}\")
set(STAGING \"${_FAT_STAGING}\")
set(MAIN_JAR \"${_MAIN_JAR}\")
set(DEP_LIST \"${_FAT_JARS_LIST}\")
set(FAT_JAR \"${_FAT_JAR}\")
set(MANIFEST \"${_FAT_MANIFEST}\")
")

   # Assembly script: extracts all jars, cleans signatures, repackages
   SET(_FAT_SCRIPT "${CMAKE_BINARY_DIR}/assemble-fat-jar.cmake")
   FILE(WRITE "${_FAT_SCRIPT}"
"include(\${CMAKE_CURRENT_LIST_DIR}/fat-jar-config.cmake)

# Clean and create staging area
file(REMOVE_RECURSE \"\${STAGING}\")
file(MAKE_DIRECTORY \"\${STAGING}\")

# Read the dependency jar list
file(STRINGS \"\${DEP_LIST}\" _JARS)

# Extract each dependency jar
foreach(_JAR \${_JARS})
   execute_process(COMMAND \"\${JAR_EXE}\" xf \"\${_JAR}\"
                   WORKING_DIRECTORY \"\${STAGING}\"
                   RESULT_VARIABLE _RC)
   if(_RC)
      message(FATAL_ERROR \"Failed to extract \${_JAR}\")
   endif()
endforeach()

# Extract main jar last so its classes and resources take priority
execute_process(COMMAND \"\${JAR_EXE}\" xf \"\${MAIN_JAR}\"
                WORKING_DIRECTORY \"\${STAGING}\"
                RESULT_VARIABLE _RC)
if(_RC)
   message(FATAL_ERROR \"Failed to extract \${MAIN_JAR}\")
endif()

# Remove signature files from dependencies (invalid after repackaging)
file(GLOB _SIG_FILES \"\${STAGING}/META-INF/*.SF\"
                     \"\${STAGING}/META-INF/*.DSA\"
                     \"\${STAGING}/META-INF/*.RSA\"
                     \"\${STAGING}/META-INF/*.EC\")
if(_SIG_FILES)
   file(REMOVE \${_SIG_FILES})
endif()

# Package the fat jar
execute_process(COMMAND \"\${JAR_EXE}\" cfm \"\${FAT_JAR}\" \"\${MANIFEST}\" .
                WORKING_DIRECTORY \"\${STAGING}\"
                RESULT_VARIABLE _RC)
if(_RC)
   message(FATAL_ERROR \"Failed to create \${FAT_JAR}\")
endif()
")

   ADD_CUSTOM_COMMAND(
      OUTPUT "${_FAT_JAR}"
      DEPENDS ${_TARGET_NAME}
      COMMAND "${CMAKE_COMMAND}" -P "${_FAT_SCRIPT}"
      COMMENT "Packaging ${_FAT_JAR_NAME}"
   )

   ADD_CUSTOM_TARGET(${_TARGET_NAME}-fat ALL DEPENDS "${_FAT_JAR}")

   INSTALL(PROGRAMS "${_FAT_JAR}" DESTINATION ${CSR_BINDIR})

   # Warn about missing JOGL native jars: the fat jar's 3D view will fail
   # at runtime on any platform whose native jars were not present at build time
   SET(_JOGL_ALL_PLATFORMS windows-amd64 linux-amd64 linux-aarch64 macosx-universal)
   FOREACH(_PLATFORM ${_JOGL_ALL_PLATFORMS})
      SET(_MISSING)
      FOREACH(_JAR gluegen-rt-natives-${_PLATFORM}.jar jogl-all-natives-${_PLATFORM}.jar)
         IF(NOT EXISTS "${CMAKE_CURRENT_LIST_DIR}/${_JAR}")
            LIST(APPEND _MISSING ${_JAR})
         ENDIF()
      ENDFOREACH()
      IF(_MISSING)
         MESSAGE(WARNING "Fat jar will have no 3D view on ${_PLATFORM}: missing ${_MISSING}")
      ENDIF()
   ENDFOREACH()
   # ========= end Fat JAR

   INSTALL_JAR(${_TARGET_NAME} DESTINATION ${CSR_BINDIR})

   IF(_STANDALONE)
      INSTALL(FILES ${JAVA_JARS} ${_JOGL_NATIVE_JARS} DESTINATION ${CSR_BINDIR})
   ELSE(_STANDALONE)
      # install the jar dependencies
      CSR_INSTALL_FILES(BIN FILES ${JAVA_JARS} ${_JOGL_NATIVE_JARS})
   ENDIF(_STANDALONE)

   # TODO remove this when CMake issue 23668 is fixed
   # INSTALL(PROGRAMS ...) will make the jar executable
   # which is needed on some systems
   # https://gitlab.kitware.com/cmake/cmake/-/issues/23668
   get_target_property(_jarFile ${_TARGET_NAME} JAR_FILE)
   INSTALL(PROGRAMS ${_jarFile} DESTINATION ${CSR_BINDIR})
   # ========= end of TODO section

   # ========= Debug
   #get_target_property(_jarFile ${_TARGET_NAME} JAR_FILE)
   #get_target_property(_classDir ${_TARGET_NAME} CLASSDIR)

   #message(STATUS "Jar file ${_jarFile}")
   #message(STATUS "Class compiled to ${_classDir}")
   # ========= Debug


   # add_custom_target() makes the java sources available in the generated IDE project
   ADD_CUSTOM_TARGET(${_TARGET_NAME}-src SOURCES ${_JAVA_SRC} ${_PNGS} ${_ICONS} META-INF/MANIFEST.MF)

   # https://github.com/beanshell/beanshell/releases
   # http://www.jdom.org/dist/binary/archive/jdom-1.1.3.zip
   # http://www.java2s.com/Code/JarDownload/jgoodies/jgoodies-common-1.8.1.jar.zip
   # http://www.java2s.com/Code/JarDownload/jgoodies/jgoodies-looks-2.5.3.jar.zip
   # https://jogamp.org/deployment/v2.6.0/jar/ (gluegen-rt, jogl-all and their -natives- jars)

ELSE(Java_Development_FOUND AND Java_FOUND)
   MESSAGE(WARNING "Java_FOUND = ${Java_FOUND}")
   MESSAGE(WARNING "Java_Development_FOUND = ${Java_Development_FOUND}")
   MESSAGE(WARNING "TrackEditor NOT included!")
ENDIF(Java_Development_FOUND AND Java_FOUND)
