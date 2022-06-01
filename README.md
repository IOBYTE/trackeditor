This is a fork of TORCS TrackEditor v0.6.2c that adds Speed Dreams specific information to the generated xml file.

The ultimate goal is be able to import and edit existing hand edited tracks and then export them without loosing any information. 

The following attributes can now be edited:
1. Track category: circuit, development, dirt, gprix, karting, oval, road, speedway, or test
2. Track version: 3, 4, or 5
3. Pit pit style: 0, 1, 2, or 3
4. Pit start buildings, stop buildings, max pits, pit indicator, speed limit
5. Segment grade, z start left, z start right, z end left, z end right, banking start, banking end
6. Segment profil, profil start tangent, profil end tangent, profil steps, profil steps length
7. Terrain Generation track step, border margin, border step, border height, orientation
8. Turn Marks width, height, vertical space, horizontal space

The following bugs have been fixed:
1. Segment z start and z end now read from xml file
2. Fix null button exception for show start point menu item

Progress:

It is now possible to create tracks with grade and camber and edit them.  It is not yet possible to edit optimized hand edited tracks.
