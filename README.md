This is a fork of TORCS TrackEditor v0.6.2c that adds Speed Dreams specific information to the generated xml file.

The ultimate goal is be able to import and edit existing hand edited tracks and then export them without loosing any information. 

The following attributes can now be edited:
1. Track category: circuit, development, dirt, gprix, karting, oval, road, speedway, or test
2. Track version: 3, 4, or 5
3. Pit start buildings
4. Pit stop buildings
5. Pit max pits
6. Segment grade
7. Segment profil start tangent
8. Segment profil end tangent

The following bugs have been fixed:
1. Segment z start and z end now read from xml file
2. Fix null button exception for show start point menu item
