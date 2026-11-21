# HTTP
Use of chunked encoding for the web page transfer.
Use of base64 encoding for the image transfer.

TCP request independant, no keep-alive. Game state is kept on server side and authentification with cookies. Cookie livetime of 10 minutes.
Use only to get the websocket connection
should at least use the following return codes: 101, 200, 303, 400, 404,
405, 411, 501 and 505

# Websocket
Exchange the player's actoins and the server's responses
New command compare to project 1, STATUS
See Websocket.java

# Web page 
Acts as the client

Message the user if JavaScript is disabled in its browser.

redirection mechanism to redirect to `play.html`

Needs to contain
- game logo
- form text field to update the player name
- form to place a bet (sends BET request when submitted)
- hit button
- stand button
- display player and dealer hands, their totals and the player bankroll


# Server
Always check the validity of the client's request

Setup a time out to avoid a zombie client that keeps resources.

Use of a thread pool
