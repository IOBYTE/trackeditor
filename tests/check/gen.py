#!/usr/bin/env python3
"""Writes the track checker's test tracks: one small oval, and copies of it each with one thing wrong."""
import os, struct, sys, zlib

OUT = sys.argv[1] if len(sys.argv) > 1 else os.path.join(os.path.dirname(os.path.abspath(__file__)), 'cases')

def png(path, rgb=(128, 128, 128), size=2):
    def chunk(kind, data):
        return struct.pack('>I', len(data)) + kind + data + struct.pack('>I', zlib.crc32(kind + data) & 0xffffffff)
    row = b'\x00' + bytes(rgb) * size
    data = b'\x89PNG\r\n\x1a\n' + chunk(b'IHDR', struct.pack('>IIBBBBB', size, size, 8, 2, 0, 0, 0)) \
        + chunk(b'IDAT', zlib.compress(row * size)) + chunk(b'IEND', b'')
    with open(path, 'wb') as f:
        f.write(data)

def num(name, val, unit=None):
    return '<attnum name="%s"%s val="%s"/>' % (name, ' unit="%s"' % unit if unit else '', val)

def text(name, val):
    return '<attstr name="%s" val="%s"/>' % (name, val)

def section(name, *lines):
    out = ['<section name="%s">' % name]
    for line in lines:
        for part in (line if isinstance(line, list) else [line]):
            out.extend('  ' + p for p in part.split('\n'))
    out.append('</section>')
    return '\n'.join(out)

def surface(name, texture):
    return section(name, num('color R1', 0.5), num('color G1', 0.5), num('color B1', 0.5),
        text('texture name', texture), text('texture type', 'discrete'), num('texture size', 10, 'm'),
        num('friction', 1.1), num('rolling resistance', 0.001), num('roughness', 0, 'm'), num('roughness wavelength', 1, 'm'))

def grip(name, texture, friction=1.1, rolling=0.001, roughness=0, wavelength=1):
    """a surface with the given grip values; None leaves one out"""
    values = [('friction', friction, None), ('rolling resistance', rolling, None), ('roughness', roughness, 'm'), ('roughness wavelength', wavelength, 'm')]
    return section(name, num('color R1', 0.5), num('color G1', 0.5), num('color B1', 0.5),
        text('texture name', texture), text('texture type', 'discrete'), num('texture size', 10, 'm'),
        *[num(key, val, unit) for key, val, unit in values if val is not None])

def side(which, start=4, end=4, border=None, barrier=None):
    return [section(which + ' Side', num('start width', start, 'm'), num('end width', end, 'm'), text('surface', 'grass')),
        section(which + ' Border', *(border or [num('width', 0.5, 'm'), num('height', 0.05, 'm'), text('surface', 'curb'), text('style', 'curb')])),
        section(which + ' Barrier', *(barrier or [num('width', 0.25, 'm'), num('height', 1, 'm'), text('surface', 'wall'), text('style', 'wall')]))]

def straight(name, length=100, extra=(), left=None, right=None, surface='road'):
    return section(name, text('type', 'str'), num('lg', length, 'm'), text('surface', surface), *extra,
        *(left or side('Left')), *(right or side('Right')))

def curve(name, arc=180, radius=50, kind='rgt', extra=(), left=None, right=None):
    return section(name, text('type', kind), num('arc', arc, 'deg'), num('radius', radius, 'm'), text('surface', 'road'), *extra,
        *(left or side('Left')), *(right or side('Right')))

def camera(name, segment, start, end, to_start=10, height=5):
    return section(name, text('segment', segment), num('to right', -5, 'm'), num('to start', to_start, 'deg' if segment.startswith('turn') else 'm'),
        num('height', height, 'm'), text('fov start', start), text('fov end', end))

def track(name, segments=None, cameras=None, surfaces=None, objects='', pits='', main_extra=(), terrain='', graphic_extra=(),
        main_width=10, category='road', header_extra=()):
    segments = segments if segments is not None else [straight('front'), curve('turn 1'), straight('back'), curve('turn 2')]
    cameras = cameras if cameras is not None else [camera('cam 1', 'front', 'front', 'back'), camera('cam 2', 'back', 'back', 'front')]
    surfaces = surfaces if surfaces is not None else [surface('road', 'road.png'), surface('grass', 'grass.png'),
        surface('curb', 'curb.png'), surface('wall', 'wall.png')]
    main_side = lambda which: [section(which + ' Side', num('width', 4, 'm'), text('surface', 'grass')),
        section(which + ' Border', num('width', 0.5, 'm'), num('height', 0.05, 'm'), text('surface', 'curb'), text('style', 'curb')),
        section(which + ' Barrier', num('width', 0.25, 'm'), num('height', 1, 'm'), text('surface', 'wall'), text('style', 'wall'))]
    body = '\n'.join([
        section('Surfaces', *surfaces),
        section('Objects', objects) if objects else '<section name="Objects"/>',
        section('Header', text('name', name), text('category', category), num('version', 4), text('author', 'track checker tests'),
            text('description', 'A test track'), *header_extra),
        section('Graphic', text('3d description', name + '.ac'), *graphic_extra,
            section('Terrain Generation', terrain) if terrain else '<section name="Terrain Generation"/>',
            '<section name="Environment Mapping"/>'),
        section('Main Track', num('width', main_width, 'm'), num('profil steps length', 4, 'm'), text('surface', 'road'), *main_extra,
            *main_side('Left'), *main_side('Right'),
            section('Pits', pits) if pits else '<section name="Pits"/>',
            section('Track Segments', *segments)),
        section('Cameras', *cameras) if cameras else '<section name="Cameras"/>'])
    return '<?xml version="1.0" encoding="UTF-8"?>\n<!-- A test track for the track checker: written by gen.py -->\n' \
        '<params name="%s" type="param" mode="mw">\n%s\n</params>\n' % (name, '\n'.join('  ' + l for l in body.split('\n')))

def write(case, args, xml, name='oval', files=('road.png', 'grass.png', 'curb.png', 'wall.png'), ac=True, where='', out=None):
    out = out or OUT
    folder = os.path.join(out, case, where, name)
    os.makedirs(folder, exist_ok=True)
    with open(os.path.join(folder, name + '.xml'), 'w', newline='\n') as f:
        f.write(xml)
    for file in files:
        png(os.path.join(folder, file))
    if ac:
        with open(os.path.join(folder, name + '.ac'), 'w', newline='\n') as f:
            f.write('AC3Db\nOBJECT world\nkids 0\n')
    with open(os.path.join(out, case, 'args.txt'), 'w', newline='\n') as f:
        f.write(args + '\n')

def oval(front=None, turn1=None, back=None, turn2=None):
    return [front or straight('front'), turn1 or curve('turn 1'), back or straight('back'), turn2 or curve('turn 2')]

def figure_eight(climb=0):
    """front crosses back halfway along both: front east, a left turn of 270 degrees, back south, a right turn of 270
    degrees back to the start. With a climb, turn 1 climbs it and turn 2 comes down again, so back crosses over front."""
    heights = lambda start, end: [num('z start', start, 'm'), num('z end', end, 'm')]
    return [straight('front', extra=heights(0, 0)), curve('turn 1', arc=270, kind='lft', extra=heights(0, climb)),
        straight('back', extra=heights(climb, climb)), curve('turn 2', arc=270, extra=heights(climb, 0))]

def hill(grade, linear=False):
    """an oval whose front climbs the grade (%) on average over its 100 m and whose back comes down again. The
    game's spline profile is steepest halfway, at one and a half times the average; a linear one keeps the grade
    and bends where each straight meets a curve: a crest at the top, a dip at the bottom"""
    profil = [text('profil', 'linear')] if linear else []
    heights = lambda start, end: [num('z start', start, 'm'), num('z end', end, 'm')] + profil
    return oval(front=straight('front', extra=heights(0, grade)), turn1=curve('turn 1', extra=heights(grade, grade)),
        back=straight('back', extra=heights(grade, 0)), turn2=curve('turn 2', extra=heights(0, 0)))

def with_default_surfaces(xml):
    """the track includes the game's default surfaces, as the games' own tracks do"""
    xml = xml.replace('<params name=', '<!DOCTYPE params SYSTEM "../../../src/libs/tgf/params.dtd" [\n'
        '<!ENTITY default-surfaces SYSTEM "../../../data/tracks/surfaces.xml">\n]>\n<params name=', 1)
    return xml.replace('<section name="Surfaces">', '<section name="Surfaces">\n    &default-surfaces;', 1)

def pit_section(**changes):
    values = {'side': 'right', 'entry': 'turn 2', 'start': 'front', 'end': 'front', 'exit': 'turn 1',
        'length': 10, 'width': 5, 'pit style': 1}
    values.update(changes)
    lines = []
    for key, value in values.items():
        if value is None:
            continue
        lines.append(num(key, value, 'm') if key in ('length', 'width') else num(key, value) if key == 'pit style' else text(key, value))
    return '\n'.join(lines)

SD = '--game sd oval/oval.xml'
IN_GAME = '--game sd game/tracks/road/oval/oval.xml'

# the least a game's data folder has: the default surfaces and objects, here none
DATA_FOLDER = {'game/data/tracks/surfaces.xml': '<section name="Default Surfaces"/>\n', 'game/data/tracks/objects.xml': '<section name="Default Objects"/>\n'}

CASES = [
    # (case, the checker's arguments, the track, other files to leave out or add)
    ('clean', SD, track('oval')),
    ('clean-torcs', '--game torcs oval/oval.xml', track('oval')),
    ('clean-csr', '--game csr oval/oval.xml', track('oval')),
    # CSR's version 4 loader: a border can widen along a segment, like a side; the editor uses it with --variable-borders
    ('border-variable-width-csr', '--game csr --variable-borders oval/oval.xml', track('oval', oval(front=straight('front',
        left=side('Left', border=[num('start width', 0.5, 'm'), num('end width', 2, 'm'), num('height', 0.05, 'm'), text('surface', 'curb'), text('style', 'curb')]))))),
    ('border-variable-width-off-csr', '--game csr oval/oval.xml', track('oval', oval(front=straight('front',
        left=side('Left', border=[num('start width', 0.5, 'm'), num('end width', 2, 'm'), num('height', 0.05, 'm'), text('surface', 'curb'), text('style', 'curb')]))))),
    # a border's width is both of its widths, so CSR doesn't read a start or end width with it
    ('border-width-and-start-end-csr', '--game csr --show saving oval/oval.xml', track('oval', oval(front=straight('front',
        left=side('Left', border=[num('width', 1, 'm'), num('start width', 0.5, 'm'), num('end width', 2, 'm'), num('height', 0.05, 'm'), text('surface', 'curb'), text('style', 'curb')]))))),
    # a tapered curb keeps the slope of its wide end: 0.1 m in 1 m here, as the 0.05 m in 0.5 m curb after it
    ('curb-taper-slope-matches-csr', '--game csr --variable-borders oval/oval.xml', track('oval', oval(front=straight('front',
        left=side('Left', border=[num('start width', 0.25, 'm'), num('end width', 1, 'm'), num('height', 0.1, 'm'), text('surface', 'curb'), text('style', 'curb')]))))),
    # it narrows from 1 m to 0.5 m, 0.05 m high: 5%, but the 0.05 m in 0.5 m curb after it is 10%
    ('curb-taper-slope-differs-csr', '--game csr --variable-borders oval/oval.xml', track('oval', oval(front=straight('front',
        right=side('Right', border=[num('start width', 1, 'm'), num('end width', 0.5, 'm'), num('height', 0.05, 'm'), text('surface', 'curb'), text('style', 'curb')]))))),
    # a border's height can change along a segment too: 0.05 m to 0.1 m here, meeting the curbs on both sides
    ('border-variable-height-csr', '--game csr --variable-borders oval/oval.xml', track('oval', oval(front=straight('front',
        left=side('Left', border=[num('width', 0.5, 'm'), num('start height', 0.05, 'm'), num('end height', 0.1, 'm'), text('surface', 'curb'), text('style', 'curb')])),
        turn1=curve('turn 1', left=side('Left', border=[num('width', 0.5, 'm'), num('height', 0.1, 'm'), text('surface', 'curb'), text('style', 'curb')]))))),
    # it rises to 0.1 m, but the 0.05 m curb after it doesn't
    ('border-variable-height-differs-csr', '--game csr --variable-borders oval/oval.xml', track('oval', oval(front=straight('front',
        right=side('Right', border=[num('width', 0.5, 'm'), num('start height', 0.05, 'm'), num('end height', 0.1, 'm'), text('surface', 'curb'), text('style', 'curb')]))))),
    # the same start and end height is saved as the height
    ('border-start-end-height-same-csr', '--game csr --variable-borders --show saving oval/oval.xml', track('oval', oval(front=straight('front',
        left=side('Left', border=[num('width', 0.5, 'm'), num('start height', 0.05, 'm'), num('end height', 0.05, 'm'), text('surface', 'curb'), text('style', 'curb')]))))),
    # a border's height is both of its heights, so CSR doesn't read a start or end height with it
    ('border-height-and-start-end-csr', '--game csr --show saving oval/oval.xml', track('oval', oval(front=straight('front',
        left=side('Left', border=[num('width', 0.5, 'm'), num('height', 0.05, 'm'), num('start height', 0.02, 'm'), num('end height', 0.1, 'm'), text('surface', 'curb'), text('style', 'curb')]))))),
    # the right border of the right turn widens to 12 m, so at the turn's end its side reaches past the curve's center
    ('border-variable-width-past-curve-center-csr', '--game csr --variable-borders oval/oval.xml', track('oval', oval(turn1=curve('turn 1', radius=20,
        right=side('Right', border=[num('start width', 0.5, 'm'), num('end width', 12, 'm'), num('height', 0.05, 'm'), text('surface', 'curb'), text('style', 'curb')])),
        turn2=curve('turn 2', radius=20)))),

    # segments
    ('straight-no-length', SD, track('oval', oval(front=section('front', text('type', 'str'), text('surface', 'road'), *side('Left'), *side('Right'))))),
    ('straight-negative-length', SD, track('oval', oval(front=straight('front', -100)))),
    ('curve-radius-zero', SD, track('oval', oval(turn1=curve('turn 1', radius=0)))),
    ('curve-no-arc', SD, track('oval', oval(turn1=section('turn 1', text('type', 'rgt'), num('radius', 50, 'm'), text('surface', 'road'), *side('Left'), *side('Right'))))),
    ('curve-radius-less-than-half-width', SD, track('oval', oval(turn1=curve('turn 1', radius=3)))),
    ('curve-more-than-a-full-turn', SD, track('oval', oval(turn1=curve('turn 1', arc=540)))),
    ('curve-arc-over-limit', SD, track('oval', oval(turn1=curve('turn 1', arc=1980)))),
    ('steps-length-negative', SD, track('oval', oval(front=straight('front', extra=[num('profil steps length', -1, 'm')])))),
    ('steps-length-too-short', SD, track('oval', oval(front=straight('front', extra=[num('profil steps length', 0.01, 'm')])))),
    ('main-width-zero', SD, track('oval', main_width=0)),
    ('unknown-segment-type', SD, track('oval', oval(front=section('front', text('type', 'zigzag'), num('lg', 100, 'm'), text('surface', 'road'), *side('Left'), *side('Right'))))),

    # heights and banking
    ('height-start-and-left-right', SD, track('oval', oval(front=straight('front', extra=[num('z start', 0, 'm'), num('z start left', 1, 'm'), num('z start right', 1, 'm')])))),
    ('height-end-does-not-match-next', SD, track('oval', oval(front=straight('front', extra=[num('z start', 0, 'm'), num('z end', 5, 'm')]),
        turn1=curve('turn 1', extra=[num('z start', 2, 'm'), num('z end', 0, 'm')])))),
    ('height-last-does-not-match-first', SD, track('oval', oval(turn2=curve('turn 2', extra=[num('z end', 3, 'm')])))),
    ('banking-and-left-right-heights', SD, track('oval', oval(front=straight('front', extra=[num('banking start', 5, 'deg'), num('z start left', 1, 'm'), num('z start right', 0, 'm')])))),
    ('grade-and-end-heights', SD, track('oval', oval(front=straight('front', extra=[num('grade', 2, '%'), num('z end left', 1, 'm'), num('z end right', 1, 'm')])))),

    # sides, borders and barriers
    ('border-plan-with-height', SD, track('oval', oval(front=straight('front',
        left=side('Left', border=[num('width', 0.5, 'm'), num('height', 0.2, 'm'), text('surface', 'curb'), text('style', 'plan')]))))),
    ('border-style-unknown', SD, track('oval', oval(front=straight('front',
        left=side('Left', border=[num('width', 0.5, 'm'), num('height', 0.05, 'm'), text('surface', 'curb'), text('style', 'kerb')]))))),
    ('barrier-fence-with-width', SD, track('oval', oval(front=straight('front',
        right=side('Right', barrier=[num('width', 0.5, 'm'), num('height', 1, 'm'), text('surface', 'wall'), text('style', 'fence')]))))),
    ('barrier-style-unknown', SD, track('oval', oval(front=straight('front',
        right=side('Right', barrier=[num('width', 0.25, 'm'), num('height', 1, 'm'), text('surface', 'wall'), text('style', 'hedge')]))))),
    ('barrier-wall-after-fence-no-width', SD, track('oval', oval(
        front=straight('front', right=side('Right', barrier=[num('height', 1, 'm'), text('surface', 'wall'), text('style', 'fence')])),
        turn1=curve('turn 1', right=side('Right', barrier=[num('height', 1, 'm'), text('surface', 'wall'), text('style', 'wall')]))))),
    ('side-reaches-curve-center', SD, track('oval', oval(turn1=curve('turn 1', radius=8, right=side('Right', start=10, end=10))))),
    # grip: a barrier with no grip isn't reported, a road with none is
    ('surface-no-grip', SD, track('oval', surfaces=[grip('road', 'road.png', friction=0), surface('grass', 'grass.png'),
        surface('curb', 'curb.png'), grip('wall', 'wall.png', friction=0)])),
    ('surface-low-friction', SD, track('oval', surfaces=[grip('road', 'road.png', friction=0.1), surface('grass', 'grass.png'),
        surface('curb', 'curb.png'), grip('wall', 'wall.png', friction=0.1)])),
    ('surface-no-friction', SD, track('oval', surfaces=[surface('road', 'road.png'), grip('grass', 'grass.png', friction=None, rolling=None),
        grip('curb', 'curb.png', rolling=None), surface('wall', 'wall.png')])),
    ('surface-absurd-grip-values', SD, track('oval', surfaces=[grip('road', 'road.png', rolling=2), grip('grass', 'grass.png', roughness=-1, wavelength=0),
        grip('curb', 'curb.png', rolling=-0.1), surface('wall', 'wall.png')])),
    # a value too large to be a number (1e999: the games read it as infinity too), and a huge one
    # (a camera's height has no check of its own, and the pit style is a whole number)
    ('value-too-large', SD, track('oval', oval(front=straight('front', length='1e300'), turn1=curve('turn 1', radius='1e999')),
        [camera('cam 1', 'front', 'front', 'back', height='1e999'), camera('cam 2', 'back', 'back', 'front')], pits=pit_section(**{'pit style': '1e999'}))),
    ('value-not-a-number', SD, track('oval', cameras=[camera('cam 1', 'front', 'front', 'back', height='nan'), camera('cam 2', 'back', 'back', 'front')])),
    ('surface-not-defined', SD, track('oval', oval(front=section('front', text('type', 'str'), num('lg', 100, 'm'), text('surface', 'gravel'), *side('Left'), *side('Right'))))),

    # names
    ('segment-name-twice', SD, track('oval', [straight('front'), curve('turn'), straight('back'), curve('turn')],
        [camera('cam 1', 'front', 'front', 'back'), camera('cam 2', 'back', 'back', 'front')])),
    ('segment-name-with-slash', SD, track('oval', [straight('front/a'), curve('turn 1'), straight('back'), curve('turn 2')],
        [camera('cam 1', 'back', 'back', 'back')])),
    ('surface-name-twice', SD, track('oval', surfaces=[surface('road', 'road.png'), surface('grass', 'grass.png'),
        surface('curb', 'curb.png'), surface('wall', 'wall.png'), surface('road', 'road.png')])),

    # cameras
    ('no-cameras', SD, track('oval', cameras=[])),
    ('camera-no-segment', SD, track('oval', cameras=[section('cam 1', num('to right', -5, 'm'), num('to start', 10, 'm'), num('height', 5, 'm'),
        text('fov start', 'front'), text('fov end', 'front'))])),
    ('camera-unknown-segment', SD, track('oval', cameras=[camera('cam 1', 'hairpin', 'front', 'front')])),
    ('camera-below-ground', SD, track('oval', cameras=[camera('cam 1', 'front', 'front', 'front', height=-2)])),
    ('camera-no-height', SD, track('oval', cameras=[section('cam 1', text('segment', 'front'), num('to right', -5, 'm'), num('to start', 10, 'm'),
        text('fov start', 'front'), text('fov end', 'front'))])),
    ('camera-length-on-a-curve', SD, track('oval', cameras=[section('cam 1', text('segment', 'turn 1'), num('to right', -5, 'm'),
        num('to start', 40, 'm'), num('height', 5, 'm'), text('fov start', 'front'), text('fov end', 'front'))])),
    ('camera-past-the-end-of-a-curve', SD, track('oval', cameras=[section('cam 1', text('segment', 'turn 1'), num('to right', -5, 'm'),
        num('to start', 270, 'deg'), num('height', 5, 'm'), text('fov start', 'front'), text('fov end', 'front'))])),
    ('camera-never-used', SD, track('oval', cameras=[camera('cam 1', 'front', 'front', 'back'), camera('cam 2', 'back', 'front', 'front')])),
    ('camera-covers-part-of-the-lap', SD, track('oval', cameras=[camera('cam 1', 'front', 'front', 'back')])),

    # pits
    ('pits', SD, track('oval', pits=pit_section())),
    ('pit-no-entry', SD, track('oval', pits=pit_section(entry=None))),
    ('pit-unknown-start', SD, track('oval', pits=pit_section(start='paddock'))),
    ('pit-no-side', SD, track('oval', pits=pit_section(side=None))),
    ('pit-side-unknown', SD, track('oval', pits=pit_section(side='middle'))),
    ('pit-segments-out-of-order', SD, track('oval', pits=pit_section(entry='turn 1', exit='turn 2'))),
    ('pits-on-a-curve', SD, track('oval', pits=pit_section(entry='front', start='turn 1', end='turn 1', exit='back'))),
    # the pit side widens along the pits
    ('pits-not-parallel', SD, track('oval', oval(front=straight('front', right=side('Right', start=4, end=8))), pits=pit_section())),

    # files
    ('background-image-missing', SD, track('oval', graphic_extra=[text('background image', 'background.png')])),
    # in a game's data folder, as the game has it, the surfaces' textures are looked for too
    ('texture-missing', IN_GAME, track('oval'), {'omit': ['grass.png'], 'where': 'game/tracks/road', 'top': DATA_FOLDER}),
    ('texture-in-data-folder', IN_GAME, track('oval'), {'omit': ['grass.png'], 'where': 'game/tracks/road', 'top': dict(DATA_FOLDER, **{'game/data/textures/grass.png': None})}),
    # a default surface can't be changed in the editor: the message is about the first segment that uses it
    ('surface-default-no-grip', IN_GAME, with_default_surfaces(track('oval', oval(back=straight('back', surface='barrier')))),
        {'where': 'game/tracks/road', 'top': dict(DATA_FOLDER, **{'game/data/tracks/surfaces.xml':
            '<?xml version="1.0" encoding="UTF-8"?>\n<!-- the default surfaces: a barrier has no grip on purpose -->\n'
            + grip('barrier', 'barrier.png', friction=0) + '\n', 'game/data/textures/barrier.png': None})}),
    ('texture-full-path', SD, track('oval', surfaces=[surface('road', '/somewhere/else/road.png'), surface('grass', 'grass.png'),
        surface('curb', 'curb.png'), surface('wall', 'wall.png')])),
    ('3d-description-missing', SD, track('oval'), {'ac': False}),
    ('relief-file-missing', SD, track('oval', terrain=text('relief file', 'oval-relief.ac'))),
    ('object-map-missing', SD, track('oval', terrain=section('Object Maps', section('map 1', text('object map', 'object-map1.png'))))),
    ('object-model-missing', SD, track('oval', objects=section('tree', text('object', 'tree.ac'), num('color', '0x00FF00'), text('orientation type', 'random')))),
    ('object-color-twice', SD, track('oval', objects=section('tree', text('object', 'tree.ac'), num('color', '0x00FF00')) + '\n'
        + section('bush', text('object', 'tree.ac'), num('color', '0x00FF00'))), {'add': {'tree.ac': 'AC3Db\nOBJECT world\nkids 0\n'}}),
    # a multi-texture (.acc) model, as accc writes it: its texture is the base layer's (missing here); the empty
    # layers are accc's unquoted placeholder, and the games draw neither normal nor specular layers
    # an .acc object's base layer is its texture, not the last layer, and placeholder layers aren't textures
    ('texture-layers-acc', '--game sd --show objects oval/oval.xml', track('oval', objects=section('tree', text('object', 'tree.acc'), num('color', '0x00FF00')),
        terrain=section('Object Maps', section('map 1', text('object map', 'object-map1.png')))),
        {'green': ['object-map1.png'], 'add': {'broken.png': 'not a png\n', 'tree.acc': 'AC3Db\nMATERIAL "m" rgb 1 1 1 amb 1 1 1 emis 0 0 0 spec 0 0 0 shi 0 trans 0\nOBJECT world\nkids 1\n'
            'OBJECT poly\nname "leaf"\ntexture "broken.png" base\ntexture empty_texture_no_mapping tiled\ntexture empty_texture_no_mapping skids\n'
            'texture empty_texture_no_mapping shad\ntexture empty_texture_no_mapping normal\ntexture empty_texture_no_mapping specular\n'
            'numvert 3\n0 0 0\n1 0 0\n0 1 0\nnumsurf 1\nSURF 0x20\nmat 0\nrefs 3\n0 0 0\n1 1 0\n2 0 1\nkids 0\n'}}),
    ('texture-not-an-image', '--game sd --show objects oval/oval.xml', track('oval', objects=section('tree', text('object', 'tree.ac'), num('color', '0x00FF00')),
        terrain=section('Object Maps', section('map 1', text('object map', 'object-map1.png')))),
        {'green': ['object-map1.png'], 'add': {'broken.png': 'not a png\n', 'tree.ac': 'AC3Db\nMATERIAL "m" rgb 1 1 1 amb 1 1 1 emis 0 0 0 spec 0 0 0 shi 0 trans 0\nOBJECT world\nkids 1\n'
            'OBJECT poly\nname "leaf"\ntexture "broken.png"\nnumvert 3\n0 0 0\n1 0 0\n0 1 0\nnumsurf 1\nSURF 0x20\nmat 0\nrefs 3\n0 0 0\n1 1 0\n2 0 1\nkids 0\n'}}),

    # files the checker can't read, and what it reads differently than they say
    ('not-xml', SD, 'this is not a track file\n'),
    ('empty-file', SD, ''),
    ('no-main-track', SD, '<?xml version="1.0" encoding="UTF-8"?>\n<params name="oval" type="param" mode="mw">\n'
        '  <section name="Header">\n    <attstr name="name" val="oval"/>\n    <attstr name="category" val="road"/>\n    <attnum name="version" val="4"/>\n  </section>\n</params>\n'),
    ('number-written-as-text', SD, track('oval').replace('<attnum name="lg" unit="m" val="100"/>', '<attstr name="lg" val="100"/>', 1)),
    ('number-not-a-number', SD, track('oval').replace('<attnum name="lg" unit="m" val="100"/>', '<attnum name="lg" unit="m" val="long"/>', 1)),
    ('key-twice', SD, track('oval').replace('<attnum name="lg" unit="m" val="100"/>', '<attnum name="lg" unit="m" val="100"/>\n        <attnum name="lg" unit="m" val="120"/>', 1)),

    # where the track crosses itself
    ('crossing-flat', SD, track('oval', figure_eight())),
    ('crossing-climbs-over', SD, track('oval', figure_eight(climb=6))),
    ('crossing-bridge-torcs', '--game torcs oval/oval.xml', track('oval', figure_eight(climb=6))),
    ('crossing-too-low-for-a-bridge-torcs', '--game torcs oval/oval.xml', track('oval', figure_eight(climb=3),
        terrain=num('crossing bridge thickness', 2.5, 'm'))),
    # a loop of exactly a full turn, flat, between front and turn 1: it ends where it starts
    ('crossing-full-turn', SD, track('oval', [straight('front'), curve('loop', arc=360, radius=30, kind='lft'), curve('turn 1'),
        straight('back'), curve('turn 2')])),

    # whether it can be driven: grade, banking, crests and dips
    ('profile-too-steep-dirt', SD, track('oval', hill(45), category='dirt')),
    ('profile-steep-dirt', SD, track('oval', hill(25), category='dirt')),
    ('profile-crest-and-dip', SD, track('oval', hill(15, linear=True))),
    # the back straight banks to 20 degrees and back again in 10 m each way
    ('profile-banking-changes-fast', SD, track('oval', [straight('front'), curve('turn 1'), straight('back', 80),
        straight('bank', 10, extra=[num('banking start', 0, 'deg'), num('banking end', 20, 'deg')]),
        straight('unbank', 10, extra=[num('banking start', 20, 'deg'), num('banking end', 0, 'deg')]), curve('turn 2')])),
    ('profile-banking', SD, track('oval', oval(turn1=curve('turn 1', extra=[num('banking start', 40, 'deg'), num('banking end', 40, 'deg')])))),

    # the track menu's pictures
    ('menu-map-missing-torcs', '--game torcs oval/oval.xml', track('oval'), {'omit': ['oval.png']}),
    ('menu-map-not-an-image-torcs', '--game torcs oval/oval.xml', track('oval'), {'omit': ['oval.png'], 'add': {'oval.png': 'not a png\n'}}),
    ('menu-pictures-missing', SD, track('oval'), {'omit': ['oval.png', 'outline.png']}),
    ('menu-preview-jpg', SD, track('oval'), {'omit': ['oval.png'], 'add': {'oval.jpg': 'not a jpg\n'}}),

    # the checker's options
    ('option-unknown', '--game sd --bogus oval/oval.xml', track('oval')),
    ('option-game-unknown', '--game monaco oval/oval.xml', track('oval')),
    ('no-track-file', '--game sd missing/missing.xml', track('oval')),
    ('pattern', '--game sd */*.xml', track('oval')),
    ('show-saving', '--game sd --show saving oval/oval.xml', track('oval', main_extra=[num('friction bonus', 2)])),
    ('format-github', '--game sd --format github oval/oval.xml', track('oval', oval(turn1=curve('turn 1', radius=0)), cameras=[])),
    ('baseline', '--game sd --baseline known.txt oval/oval.xml', track('oval', oval(turn1=curve('turn 1', radius=3)), cameras=[]),
        {'top': {'known.txt': '# Track editor check baseline 2: one message per line, the track (the category its file has, its folder and file), a tab, then the message\n'
            'road/oval/oval.xml\tNo cameras. The game\'s TV views will show the cars from 120 m above the track.\n'}}),
]

if __name__ == '__main__':
    names = set()
    for entry in CASES:
        case, args, xml = entry[:3]
        options = entry[3] if len(entry) > 3 else {}
        assert case not in names, case
        names.add(case)
        # the textures, and the track menu's pictures: TORCS's map oval.png (also Speed Dreams' and CSR's
        # preview) and Speed Dreams' and CSR's outline.png
        files = [f for f in ('road.png', 'grass.png', 'curb.png', 'wall.png', 'oval.png', 'outline.png') if f not in options.get('omit', ())]
        write(case, args, xml, files=files, ac=options.get('ac', True), where=options.get('where', ''),
            out=OUT + '-waiting' if options.get('waiting') else None)
        for file in options.get('green', ()):
            png(os.path.join(OUT, case, 'oval', file), (0, 255, 0), 1)
        for file, content in options.get('add', {}).items():
            with open(os.path.join(OUT, case, 'oval', file), 'w', newline='\n') as f:
                f.write(content)
        for file, content in options.get('top', {}).items():
            path = os.path.join(OUT, case, file)
            os.makedirs(os.path.dirname(path), exist_ok=True)
            if content is None:
                png(path)
                continue
            with open(path, 'w', newline='\n') as f:
                f.write(content)
    print('%d cases written to %s' % (len(CASES), OUT))
