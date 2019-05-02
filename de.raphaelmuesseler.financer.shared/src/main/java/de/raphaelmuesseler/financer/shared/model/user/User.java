package de.raphaelmuesseler.financer.shared.model.user;

import de.raphaelmuesseler.financer.shared.model.db.SettingsDAO;
import de.raphaelmuesseler.financer.shared.model.db.TokenDAO;
import de.raphaelmuesseler.financer.shared.model.db.UserDAO;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class User extends UserDAO {
    private static final long serialVersionUID = 164741396778652317L;

    public enum Gender {
        MALE("male"),
        FEMALE("female"),
        NOT_SPECIFIED("notSpecified");

        private final String name;

        Gender(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Gender getGenderByName(String name) {
            for (Gender gender : values()) {
                if (gender.getName().equals(name)) {
                    return gender;
                }
            }
            return null;
        }

    }

    private final Settings settings;

    public User(UserDAO databaseUser) {
        this(databaseUser.getId(),
                databaseUser.getEmail(),
                databaseUser.getPassword(),
                databaseUser.getSalt(),
                databaseUser.getName(),
                databaseUser.getSurname(),
                databaseUser.getBirthDate(),
                Gender.getGenderByName(databaseUser.getGenderName()));
        if (databaseUser.getTokens() != null) {
            this.setTokens(databaseUser.getTokens());
        }
        if (databaseUser.getDatabaseSettings() != null) {
            this.setDatabaseSettings(databaseUser.getDatabaseSettings());
        }
        if (databaseUser.getCategories() != null) {
            this.setCategories(databaseUser.getCategories());
        }
    }

    public User() {
        this(-1, null, null, null, null, null, null, null);
    }

    public User(int id, String email, String password, String salt, String name, String surname, LocalDate birthDate, Gender gender) {
        this.setId(id);
        this.setEmail(email);
        this.setPassword(password);
        this.setSalt(salt);
        this.setName(name);
        this.setSurname(surname);
        this.setBirthDate(birthDate);
        if (gender != null) {
            this.setGenderName(gender.getName());
        } else {
            this.setGenderName(null);
        }

        this.settings = new UserSettings();
    }

    @Override
    public UserDAO toDatabaseAccessObject() {
        UserDAO databaseUser = new UserDAO();
        databaseUser.setId(this.getId());
        databaseUser.setEmail(this.getEmail());
        databaseUser.setPassword(this.getPassword());
        databaseUser.setSalt(this.getSalt());
        databaseUser.setName(this.getName());
        databaseUser.setSurname(this.getSurname());
        databaseUser.setBirthDate(this.getBirthDate());
        databaseUser.setGenderName(this.getGender().getName());
        databaseUser.setTokens(this.getTokens());
        databaseUser.setDatabaseSettings(this.getDatabaseSettings());
        return databaseUser;
    }

    public String getFullName() {
        return this.getName() + " " + this.getSurname();
    }

    public Settings getSettings() {
        if (this.getDatabaseSettings() != null) {
            for (SettingsDAO databaseSettings : super.getDatabaseSettings()) {
                this.settings.setValueByProperty(Settings.Property.getPropertyByName(databaseSettings.getProperty()), databaseSettings.getValue());
            }
        }
        return this.settings;
    }

    public Gender getGender() {
        return Gender.getGenderByName(this.getGenderName());
    }

    @Override
    public Set<TokenDAO> getTokens() {
        if (super.getTokens() == null) {
            this.setTokens(new HashSet<>());
        }
        return super.getTokens();
    }

    public void setGender(Gender gender) {
        this.setGenderName(gender.getName());
    }

    @Override
    public void setDatabaseSettings(Set<SettingsDAO> settings) {
        if (settings != null) {
            super.setDatabaseSettings(settings);
        }
    }
}
