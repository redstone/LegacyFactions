package net.redstoneore.legacyfactions.cmd;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.redstoneore.legacyfactions.Factions;
import net.redstoneore.legacyfactions.Permission;
import net.redstoneore.legacyfactions.config.CommandAliases;
import net.redstoneore.legacyfactions.config.Config;
import net.redstoneore.legacyfactions.expansion.FactionsExpansions;
import net.redstoneore.legacyfactions.lang.Lang;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Set;

public class CmdFactionsConfig extends FCommand {
	
	// -------------------------------------------------- //
	// INSTANCE
	// -------------------------------------------------- //
	
	private static CmdFactionsConfig instance = new CmdFactionsConfig();
	public static CmdFactionsConfig get() { return instance; }
	
	// -------------------------------------------------- //
	// STATIC FIELDS
	// -------------------------------------------------- //

	private static HashMap<String, String> properFieldNames = new HashMap<>();

	// -------------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------------- //

	private CmdFactionsConfig() {
		this.aliases.addAll(CommandAliases.cmdAliasesConfig);

		this.requiredArgs.add("setting");
		this.optionalArgs.put("[value or view/add/remove]", "view");
		
		this.errorOnToManyArgs = false;

		this.permission = Permission.CONFIG.getNode();
		this.disableOnLock = true;

		this.senderMustBePlayer = false;
		this.senderMustBeMember = false;
		this.senderMustBeModerator = false;
		this.senderMustBeColeader = false;
		this.senderMustBeAdmin = false;
	}

	// -------------------------------------------------- //
	// METHODS
	// -------------------------------------------------- //
	
	public void cacheProperFieldNames() {
		// Store a lookup map of lowercase field names paired with proper capitalisation field names
		// this way, if the person using this command messes up the capitalisation, we can fix that
		if (properFieldNames.isEmpty()) {
			Field[] fields = Config.class.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				properFieldNames.put(fields[i].getName().toLowerCase(), fields[i].getName());
			}
		}
	}
	
	@Override
	public void perform() {
		this.cacheProperFieldNames();

		String field = this.argAsString(0).toLowerCase();
		if (field.startsWith("\"") && field.endsWith("\"")) {
			field = field.substring(1, field.length() - 1);
		}
		
		String fieldName = properFieldNames.get(field);

		if (fieldName == null || fieldName.isEmpty()) {
			sendMessage(Lang.COMMAND_CONFIG_NOEXIST, field);
			return;
		}
		
		if (!this.argIsSet(1) || this.argAsString(1).equalsIgnoreCase("view")) {
			try {
				this.sendMessage(fieldName + " = " + Config.class.getField(fieldName).get(this).toString());
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
			return;
		}
		
		String success;

		String value = args.get(1);
		for (int i = 2; i < args.size(); i++) {
			value += ' ' + args.get(i);
		}

		try {
			Field target = Config.class.getField(fieldName);

			// boolean
			if (target.getType() == boolean.class) {
				boolean targetValue = this.strAsBool(value);
				target.setBoolean(null, targetValue);

				if (targetValue) {
					success = "\"" + fieldName + Lang.COMMAND_CONFIG_SET_TRUE.toString();
				} else {
					success = "\"" + fieldName + Lang.COMMAND_CONFIG_SET_FALSE.toString();
				}
			}

			// int
			else if (target.getType() == int.class) {
				try {
					int intVal = Integer.parseInt(value);
					target.setInt(null, intVal);
					success = "\"" + fieldName + Lang.COMMAND_CONFIG_OPTIONSET.toString() + intVal + ".";
				} catch (NumberFormatException ex) {
					sendMessage(Lang.COMMAND_CONFIG_INTREQUIRED.format(fieldName));
					return;
				}
			}

			// long
			else if (target.getType() == long.class) {
				try {
					long longVal = Long.parseLong(value);
					target.setLong(null, longVal);
					success = "\"" + fieldName + Lang.COMMAND_CONFIG_OPTIONSET.toString() + longVal + ".";
				} catch (NumberFormatException ex) {
					sendMessage(Lang.COMMAND_CONFIG_LONGREQUIRED.format(fieldName));
					return;
				}
			}

			// double
			else if (target.getType() == double.class) {
				try {
					double doubleVal = Double.parseDouble(value);
					target.setDouble(null, doubleVal);
					success = "\"" + fieldName + Lang.COMMAND_CONFIG_OPTIONSET.toString() + doubleVal + ".";
				} catch (NumberFormatException ex) {
					sendMessage(Lang.COMMAND_CONFIG_DOUBLEREQUIRED.format(fieldName));
					return;
				}
			}

			// float
			else if (target.getType() == float.class) {
				try {
					float floatVal = Float.parseFloat(value);
					target.setFloat(null, floatVal);
					success = "\"" + fieldName + Lang.COMMAND_CONFIG_OPTIONSET.toString() + floatVal + ".";
				} catch (NumberFormatException ex) {
					sendMessage(Lang.COMMAND_CONFIG_FLOATREQUIRED.format(fieldName));
					return;
				}
			}

			// String
			else if (target.getType() == String.class) {
				target.set(null, value);
				success = "\"" + fieldName + Lang.COMMAND_CONFIG_OPTIONSET.toString() + value + "\".";
			}

			// ChatColor
			else if (target.getType() == ChatColor.class) {
				ChatColor newColor = null;
				try {
					newColor = ChatColor.valueOf(value.toUpperCase());
				} catch (IllegalArgumentException ex) {

				}
				if (newColor == null) {
					sendMessage(Lang.COMMAND_CONFIG_INVALID_COLOUR.format(fieldName, value.toUpperCase()));
					return;
				}
				target.set(null, newColor);
				success = "\"" + fieldName + Lang.COMMAND_CONFIG_COLOURSET.toString() + value.toUpperCase() + "\".";
			}

			// Set<?> or other parameterized collection
			else if (target.getGenericType() instanceof ParameterizedType) {
				ParameterizedType targSet = (ParameterizedType) target.getGenericType();
				Type innerType = targSet.getActualTypeArguments()[0];

				// not a Set, somehow, and that should be the only collection we're using in Conf.java
				if (targSet.getRawType() != Set.class) {
					sendMessage(Lang.COMMAND_CONFIG_INVALID_COLLECTION.format(fieldName));
					return;
				}

				// Set<Material>
				else if (innerType == Material.class) {
					Material newMat = null;
					try {
						newMat = Material.valueOf(value.toUpperCase());
					} catch (IllegalArgumentException ex) {

					}
					if (newMat == null) {
						sendMessage(Lang.COMMAND_CONFIG_INVALID_MATERIAL.format(fieldName, value.toUpperCase()));
						return;
					}

					@SuppressWarnings("unchecked") Set<Material> matSet = (Set<Material>) target.get(null);

					// Material already present, so remove it
					if (matSet.contains(newMat)) {
						matSet.remove(newMat);
						target.set(null, matSet);
						success = Lang.COMMAND_CONFIG_MATERIAL_REMOVED.format(fieldName, value.toUpperCase());
					}
					// Material not present yet, add it
					else {
						matSet.add(newMat);
						target.set(null, matSet);
						success = Lang.COMMAND_CONFIG_MATERIAL_ADDED.format(fieldName, value.toUpperCase());
					}
				}

				// Set<String>
				else if (innerType == String.class) {
					@SuppressWarnings("unchecked") Set<String> stringSet = (Set<String>) target.get(null);

					// String already present, so remove it
					if (stringSet.contains(value)) {
						stringSet.remove(value);
						target.set(null, stringSet);
						success = Lang.COMMAND_CONFIG_SET_REMOVED.format(fieldName, value);
					}
					// String not present yet, add it
					else {
						stringSet.add(value);
						target.set(null, stringSet);
						success = Lang.COMMAND_CONFIG_SET_ADDED.format(fieldName, value);
					}
				}

				// Set of unknown type
				else {
					sendMessage(Lang.COMMAND_CONFIG_INVALID_TYPESET.format(fieldName));
					return;
				}
			}

			// unknown type
			else {
				sendMessage(Lang.COMMAND_CONFIG_ERROR_TYPE.format(fieldName, target.getClass().getName()));
				return;
			}
		} catch (NoSuchFieldException ex) {
			sendMessage(Lang.COMMAND_CONFIG_ERROR_MATCHING.format(fieldName));
			return;
		} catch (IllegalAccessException ex) {
			sendMessage(Lang.COMMAND_CONFIG_ERROR_SETTING.format(fieldName, value));
			return;
		}

		if (!success.isEmpty()) {
			if (this.sender instanceof Player) {
				this.sendMessage(success);
				Factions.get().log(success + Lang.COMMAND_CONFIG_LOG.format((Player) sender));
			} else {
				 // using P.get().log() instead of sendMessage if run from server console so that "[Factions v#.#.#]" is prepended in server log
				Factions.get().log(success);
			}
			FactionsExpansions.sync();
		}
		// save change to disk
		Config.save();
	}

	@Override
	public String getUsageTranslation() {
		return Lang.COMMAND_CONFIG_DESCRIPTION.toString();
	}

}
