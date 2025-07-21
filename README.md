# LayeredyAnnounce Minecraft Plugin

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/plasma-services/layeredy-announce-plugin)
[![Minecraft](https://img.shields.io/badge/minecraft-1.20.6+-green.svg)](https://www.spigotmc.org/)
[![License](https://img.shields.io/badge/license-MIT-orange.svg)](LICENSE)

A powerful Minecraft plugin that integrates with [Layeredy Announce](https://announce.layeredy.com/) to display announcements as customizable boss bars in your server.

**Developed by [plasma.services](https://plasma.services)**

---

## 📋 Table of Contents

- [Features](#-features)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Commands](#-commands)
- [Permissions](#-permissions)
- [API Integration](#-api-integration)
- [Boss Bar Customization](#-boss-bar-customization)
- [Troubleshooting](#-troubleshooting)
- [Building from Source](#-building-from-source)
- [Contributing](#-contributing)
- [Support](#-support)
- [License](#-license)

---

## 🚀 Features

- **🔔 Real-time Announcements**: Automatically fetches announcements from Layeredy Announce API
- **📊 Boss Bar Display**: Shows announcements as eye-catching, customizable boss bars
- **⚙️ Flexible Configuration**: Fully configurable colors, styles, durations, and check intervals
- **🎯 Permission System**: Control who can see announcements and manage the plugin
- **🔄 Smart Caching**: Respects `showOnce` flags and prevents duplicate announcements
- **🛠️ Admin Commands**: Comprehensive command suite for testing and management
- **🐛 Debug Mode**: Detailed logging for troubleshooting and monitoring
- **⚡ Performance Optimized**: Asynchronous API calls with minimal server impact
- **🔗 Direct Integration**: Works seamlessly with the Layeredy Announce web service

---

## 📦 Installation

### Prerequisites
- Minecraft server running **Spigot 1.20.4+**, **Paper**, or compatible software
- Java 17 or higher
- Active [Layeredy Announce](https://announce.layeredy.com/) account

### Steps
1. **Download** the latest plugin JAR from [Releases](https://github.com/plasma-services/layeredy-announce-plugin/releases)
2. **Place** the JAR file in your server's `plugins` folder
3. **Restart** your server
4. **Configure** the plugin (see [Configuration](#-configuration))
5. **Reload** with `/layeredyannounce reload`

---

## ⚙️ Configuration

The plugin creates `plugins/LayeredyAnnounce/config.yml` with the following options:

```yaml
# Enable or disable the plugin functionality
enabled: true

# Your Layeredy Announce user ID
# Get this from your dashboard at https://announce.layeredy.com/
# Format: usr_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
user-id: "usr_db00e0d23c07d1b4b2d243c9702c896d"

# How often to check for new announcements (in seconds)
# Minimum: 30 seconds to avoid rate limiting
check-interval: 60

# Layeredy Announce API URL
# Default endpoint - usually no need to change
api-url: "https://announce.layeredy.com/api/announce"

# Boss bar display settings
bossbar:
  # How long to display each announcement (in seconds)
  # Minimum: 5 seconds
  duration: 10
  
  # Boss bar color options:
  # BLUE, GREEN, PINK, PURPLE, RED, WHITE, YELLOW
  color: "BLUE"
  
  # Boss bar style options:
  # SOLID, SEGMENTED_6, SEGMENTED_10, SEGMENTED_12, SEGMENTED_20
  style: "SOLID"

# Enable detailed debug logging
debug: false
```

### Getting Your User ID

1. Visit [announce.layeredy.com](https://announce.layeredy.com/)
2. **Sign up** or **log in** to your account
3. Find your **User ID** in the dashboard
4. Copy the ID (format: `usr_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx`)
5. Paste it into your `config.yml`

---

## 🎮 Commands

All commands support tab completion and require appropriate permissions.

| Command | Description | Permission |
|---------|-------------|------------|
| `/layeredyannounce reload` | Reload plugin configuration | `layeredyannounce.admin` |
| `/layeredyannounce status` | Show detailed plugin status | `layeredyannounce.admin` |
| `/layeredyannounce test` | Test API connection & display sample boss bar | `layeredyannounce.admin` |
| `/layeredyannounce check` | Manually check for new announcements | `layeredyannounce.admin` |
| `/layeredyannounce clear` | Clear all active boss bars and cache | `layeredyannounce.admin` |
| `/layeredyannounce debug <on\|off>` | Toggle debug mode | `layeredyannounce.admin` |
| `/layeredyannounce help` | Show command help | `layeredyannounce.admin` |

**Aliases:** `/announce`, `/lannounce`

---

## 🔐 Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `layeredyannounce.admin` | Access to all admin commands | `op` |
| `layeredyannounce.see` | Can see announcement boss bars | `true` |

---

## 🔗 API Integration

### Endpoint
The plugin connects to:
```
https://announce.layeredy.com/api/announce/{user_id}
```

### Response Format
The API returns a single announcement object:

```json
{
  "hasAnnouncement": true,
  "id": "ann_b612aa26149b0db0b6648b755dca4de2",
  "content": "50% off all products! https://plasma.services",
  "theme": "default",
  "customColor": "#3b82f6",
  "customTextColor": "#ffffff",
  "opacity": 1,
  "position": "top",
  "showOnce": false,
  "allowDismissal": true,
  "linkUrl": "",
  "linkText": "",
  "userPlan": "plus"
}
```

### Key Fields
- **`hasAnnouncement`**: Boolean indicating if there's an active announcement
- **`id`**: Unique announcement identifier (format: `ann_xxxxx`)
- **`content`**: The message displayed in the boss bar
- **`showOnce`**: If true, announcement is only shown once per session
- **`customColor`**: Hex color for future styling features

---

## 🎨 Boss Bar Customization

### Colors
- **BLUE** (default) - Professional blue
- **GREEN** - Success/positive messages
- **YELLOW** - Warnings/attention
- **RED** - Urgent/important
- **PURPLE** - Special events
- **PINK** - Fun/casual
- **WHITE** - Neutral/clean

### Styles
- **SOLID** (default) - Clean, continuous bar
- **SEGMENTED_6** - 6 distinct segments
- **SEGMENTED_10** - 10 segments for progress-like appearance
- **SEGMENTED_12** - 12 segments
- **SEGMENTED_20** - 20 segments for fine detail

---

## 🔍 Troubleshooting

### No Announcements Showing

1. **Verify User ID**
   ```bash
   # Check your config.yml has the correct format
   user-id: "usr_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
   ```

2. **Test API Manually**
   ```bash
   # Visit in browser or curl:
   https://announce.layeredy.com/api/announce/YOUR_USER_ID
   # Should return JSON with "hasAnnouncement": true/false
   ```

3. **Check Permissions**
   ```bash
   # Ensure players have permission to see announcements
   /lp user <player> permission set layeredyannounce.see true
   ```

4. **Enable Debug Mode**
   ```bash
   /layeredyannounce debug on
   /layeredyannounce check
   # Check console for detailed logs
   ```

### API Connection Issues

1. **Test Connectivity**
   ```bash
   # From server console or in-game
   /layeredyannounce test
   ```

2. **Check Firewall**
   - Ensure outbound HTTPS (port 443) is allowed
   - Whitelist `announce.layeredy.com` if needed

3. **Verify Configuration**
   ```bash
   /layeredyannounce status
   # Check that the full endpoint URL is correct
   ```

### Boss Bars Not Visible

1. **Check Player Permissions**
   ```bash
   /layeredyannounce test
   # This should show a test boss bar
   ```

2. **Verify Duration Settings**
   ```yaml
   # In config.yml, ensure duration is reasonable
   bossbar:
     duration: 10  # At least 5 seconds
   ```

3. **Test with Different Colors/Styles**
   ```yaml
   bossbar:
     color: "RED"      # More visible
     style: "SOLID"    # Simpler style
   ```

### Common Issues

| Issue | Solution |
|-------|----------|
| "Connection test failed" | Check internet connectivity and firewall |
| "Invalid user ID format" | Ensure ID starts with `usr_` and is 32 chars |
| "No permission" | Grant `layeredyannounce.see` to players |
| "Boss bar disappears immediately" | Increase `bossbar.duration` in config |
| "Announcements repeat" | Check if `showOnce` is false in Layeredy dashboard |

---

## 🔨 Building from Source

### Requirements
- **Java 17** or higher
- **Maven 3.6+**
- **Git**

### Build Steps
```bash
# Clone the repository
git clone https://github.com/plasma-services/layeredy-announce-plugin.git
cd layeredy-announce-plugin

# Build with Maven
mvn clean package

# Find compiled JAR in target/
ls target/layeredy-announce-*.jar
```

### Development Setup
```bash
# Install dependencies
mvn clean install

# Run tests
mvn test

# Generate documentation
mvn javadoc:javadoc
```

---

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

### Quick Start
1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Code Style
- Follow Java conventions
- Add JavaDoc comments for public methods
- Include unit tests for new features
- Update documentation as needed

---

## 📞 Support

### Plugin Support
- **Issues**: [GitHub Issues](https://github.com/plasma-services/layeredy-announce-plugin/issues)
- **Discord**: [plasma.services Discord](https://discord.gg/plasma-services)
- **Email**: [support@plasma.services](mailto:support@plasma.services)

### Layeredy Announce Service
- **Website**: [announce.layeredy.com](https://announce.layeredy.com/)
- **Support**: [Layeredy Forum](https://forum.layeredy.com/forums/requests/)
- **Documentation**: [Layeredy Docs](https://docs.layeredy.com/)

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Credits

- **Developer**: [plasma.services](https://plasma.services)
- **Layeredy Announce Service**: [Layeredy Software](https://layeredy.com/)
- **Inspired by**: The Blueprint extension for Pterodactyl
- **Built with**: Spigot API, Gson, Maven

---

## 📊 Statistics

- **Supported Minecraft Versions**: 1.20.4+
- **API Calls**: Configurable interval (min 30s)
- **Memory Usage**: ~2MB
- **Performance Impact**: Minimal (async operations)

---

<div align="center">

**⭐ If you find this plugin useful, please consider starring the repository! ⭐**

Made with ❤️ by [plasma.services](https://plasma.services)

</div>
