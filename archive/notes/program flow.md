# SERVER

Planned development for future

**Some disorganized notes...**

- This mod will never be able to run server-only due to custom rendering
    - Do not send network packets to users without the mod, it will probably blow up client
- Server to take terrain generation burden, store for distribution to clients
- Require common behavior between client/server world generation - logic should be exactly the same
- Ensure server-side is optional!!

# CLIENT

Undergoing active development

## Server-based

Planned development for future

**If server has mod installed...**

1. If so, request baked generation from server only
2. Disable client-side generation, but DO cache server's baked world gen
3. When server-cached world gen is loaded, run hash function-type check to ensure up to date
4. If not up to date, replace with new cache
5. If server is tardy, client generates own temp terrain
6. When server update available, discard temp terrain

## Client-only

Undergoing active development

**If server doesn't have mod...**

1. Check if generated terrain already exists in cache. If so, load and skip to **5.**
2. Run procedural generator for chunk to look for appropriate areas to spawn terrain
3. Calculate light, normals, everything
4. Store all data in an object and ensure saved to file "cache"

    - *should all objects in the cache be grouped into chunks/big meshes? to be rendered together?*
    - *if so, massive render system rewrite required... further research required*

5. Send object to render system - render system will NOT be doing lookups or checks, ONLY RENDERING (for efficiency!)

# THREADS

Seperation of roles/responsibilities with different priority of compute resources to maintain efficient performance.

There must be multiple threads to maintain efficiency, with limitations on how much compute they can draw. After all, this is a beautification mod. Not adding much to the experience by tanking performance.

## Render (Loop)

- Load cached data for chunk(s) within player position into object
    - Cache status:
        - Never generated
        - Queued
        - Generated
        - Regenerating (provides old data in meantime)
    - If no data, call generation thread (skip)
        - Method called should add to queue
        - Should not add to queue multiple times!
        - NEVER let the render thread wait on generation! BAD BAD BAD!
- Pass loaded objects to low-level render
- Rinse and repeat


## Block update (Event listener)

- Call generation function to queue regeneration at that position

## Generation thread (Explicitly invoked)

Note: This thread has a huge potential to DEVESTATE performance. NEED to find a way to deprioritize or throttle!

- Read queue for next entry
    - Or automatically prioritize based on proxemity to players? Think about that. Probably different implementation on server & client though.
- Placement criteria:
    - Is the source block (below moss block) on the approved source list? (is it a natural block?)
    - Is the moss block (actual position) on the approved environments list? (air, mostly air block, liquids, etc)
    - If yes, continue
- Density calculation
    - As X/Z area increases, Y decreases
    - Multiply each block by seed-based noise map to add organic feel
    - Levels
        - Extremely light (size X/Z: 1-2, Y: 1) (offset middle)
            - Source block has air around it?
            - Moss block has air around it?
        - Light (size X/Z: 1-4, Y: 1-2) (offset everywhere)
            - Is the source block surrounded by blocks of the same level X/Z axis?
            - Is the moss block surrounded by air in X/Z axis?
        - Stacked Moderate (size X/Z: 1-6, Y: 1-4) (offset towards segment(s))
            - Source block surrounded
            - Moss block borders full blocks
        - Stacked High (size X/Z: 1-8, Y: 1-6) (offset towards segment(s))
            - Same as last but bordering upward sheer edge
- Place blocks
- Run lighting, etc calculations
- Save to cache
- Continue to next entry in queue

Note: If queue empty/thread idle, pre-gen moss as able for existing terrain (use limited resources)

# MISC

### LOD Gameplan (planned)

- As distance from the player increases, only show larger blocks
- Example (player distance = PD, meters = m)
    - PD less than 10m: No filter
    - PD between 10-20m: Only sizes 2-16
    - PD between 20-30m: Only sizes 3-16
    - ...so on
- This should be user configurable
- Do not just snap in when in LOD range
    - Example, PD between 20-30m: Only sizes 3-16
    - PD = 31m, size 3 not rendered
    - PD = 30m, size 3 rendered but at 0 opacity
    - PD = 29m, size 3 rendered at 25 opacity
    - PD = 28m, size 3 rendered at 50 opacity
    - PD = 27m, size 3 rendered at 75 opacity
    - PD = 26m, size 3 rendered at 100 opacity
    - multiply for smooth gradient, not stepped if statements
    - instead of opacity, could consider negative y-offset to make them "come up from the ground", or combine both effects!
- Dynamic LOD to manage framerate? Worth considering for future