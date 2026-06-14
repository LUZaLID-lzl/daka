# Explore Motion Spec

## Brief

- Source: `res/drawable-nodpi/category_art_explore.png` (500 x 500, transparent)
- Personality: curious, playful, confident
- Context: UI Lab preview for a category illustration
- Actors: ground, cloud, character, backpack, hat, telescope, map pin
- Final-frame contract: the semantic actors dissolve into the untouched source bitmap at rest

## Choreography

| Phase | Time | Motion |
| --- | ---: | --- |
| Anticipation | 0-320 ms | Ground and cloud establish the stage |
| Action | 320-1120 ms | Character, backpack, hat, and telescope assemble on overlapping arcs |
| Follow-through | 1120-1600 ms | Map pin drops with overshoot; actors resolve into the exact source frame |
| Idle | 3200 ms loop | Subtle breathing, telescope glint, and map-pin pulse |

The 1600 ms reveal follows a 20/50/30 timing shape. No two actors share the same start and end.
The idle motion starts only after the reveal lands.

## Principles

- Staging: the whole illustration arrives first; the telescope and destination pin become secondary accents.
- Slow in/slow out: the main arrival uses a strong ease-out and the overshoot uses a restrained bounce.
- Timing: reveal is 1200 ms; idle is intentionally slower and lower amplitude.
- Follow-through: scale overshoot is followed by a small counter-settle.
- Appeal: glint and map-pin pulse reinforce the exploration theme without redrawing the artwork.
- Reduced motion: when system animators are disabled, the original bitmap is shown immediately.

## Tunable Values

- Reveal duration: `1600 ms`
- Idle duration: `3200 ms`
- Actor entry scales: `0.58-0.82`
- Actor travel: `12-68 px`
- Maximum idle scale delta: `0.6%`
