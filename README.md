A lightweight authentication plugin for offline-mode Minecraft servers.

**xAuthorize** is a simple, no-fuss plugin designed to protect your offline-mode server from players pretending to be someone else. It forces every player to register with a password upon their first join and to log in with that password every time they come back.

The plugin is built with Paper 1.21.11 API, uses BCrypt for password hashing, and stores all data locally in a YAML file. No external database setup is required.

**If you run a Minecraft server in online-mode: false (for example, to allow players without a premium Minecraft account to join), you have a big problem: anyone can join with any username they want, including yours.

xAuthorize solves this by creating a custom password-based authentication system. Players must prove who they are by providing their password before they can do anything on the server.

Unlike heavier plugins like AuthMe or xAuth, xAuthorize is minimal, easy to configure, and gets the job done without unnecessary complexity.**

/register <password>	Register a new account with your chosen password.	None (all)
/login <password>	Log in to your existing account.	None (all)
/xreset <player>	Reset a player's password account (so they can re‑register).	xauth.reset
