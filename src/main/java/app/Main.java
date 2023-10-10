package main.java.app;

import main.java.exception.InvalidDataException;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/*
Напишите приложение, которое будет запрашивать у пользователя следующие данные в произвольном порядке, разделенные пробелом:
Фамилия Имя Отчество дата рождения номер телефона пол
Форматы данных:
фамилия, имя, отчество - строки
дата рождения - строка формата dd.mm.yyyy
номер телефона - целое беззнаковое число без форматирования
пол - символ латиницей f или m.
Приложение должно проверить введенные данные по количеству. Если количество не совпадает с требуемым, вернуть код ошибки, обработать его и показать пользователю сообщение, что он ввел меньше и больше данных, чем требуется.
Приложение должно попытаться распарсить полученные значения и выделить из них требуемые параметры. Если форматы данных не совпадают, нужно бросить исключение, соответствующее типу проблемы. Можно использовать встроенные типы java и создать свои. Исключение должно быть корректно обработано, пользователю выведено сообщение с информацией, что именно неверно.
Если всё введено и обработано верно, должен создаться файл с названием, равным фамилии, в него в одну строку должны записаться полученные данные, вида
<Фамилия><Имя><Отчество><датарождения> <номертелефона><пол>
Однофамильцы должны записаться в один и тот же файл, в отдельные строки.
Не забудьте закрыть соединение с файлом.
При возникновении проблемы с чтением-записью в файл, исключение должно быть корректно обработано, пользователь должен увидеть стектрейс ошибки.
 */
public class Main {
    private static final int EXPECTED_DATA_COUNT = 6;

    public static void main(String[] args) {
        try {
            saveRecord();
            System.out.print("Данные добавлены в файл");
        } catch (InvalidDataException e) {
            System.out.println(e.getMessage());
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void saveRecord() throws InvalidDataException, ParseException {
        System.out.println("Введите Фамилию, Имя, Отчество, дату рождения (в формате dd.mm.yyyy), номер телефона (целое беззнаковое число) и пол(символ латиницей f или m), разделенные пробелом");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String input = reader.readLine();
            String[] inputArray = input.split(" ");
            if (inputArray.length != EXPECTED_DATA_COUNT) {
                if (inputArray.length > EXPECTED_DATA_COUNT) {
                    throw new InvalidDataException("Введено больше данных, чем требуется");
                } else {
                    throw new InvalidDataException("Введено меньше данных, чем требуется");
                }
            }
            String lastName = null;
            String firstName = null;
            String patronymicName = null;
            String birthDate = null;
            String phoneNumber = null;
            String gender = null;
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            for (String data : inputArray) {
                if (data.matches("[a-zA-Z]+")) { // Фамилия, Имя, Отчество состоят только из букв
                    if (Objects.isNull(lastName) && !(data.equals("f") || data.equals("m"))) {
                        lastName = data;
                    } else if (Objects.isNull(firstName) && !(data.equals("f") || data.equals("m"))) {
                        firstName = data;
                    } else if (Objects.isNull(patronymicName) && !(data.equals("f") || data.equals("m"))) {
                        patronymicName = data;
                    } else if (Objects.isNull(gender) && data.matches("[fm]")) { // Пол - символ 'f' или 'm'
                        gender = data;
                    }
                } else if (data.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) { // Дата рождения в формате dd.mm.yyyy
                    birthDate = data;
                } else if (data.matches("\\d+")) { // Номер телефона состоит только из цифр
                    phoneNumber = data;
                } else {
                    throw new InvalidDataException("Ошибка: неверный формат данных");
                }
            }
            if (Objects.isNull(lastName) || Objects.isNull(firstName) || Objects.isNull(patronymicName)) {
                throw new InvalidDataException("Неверный формат Фамилии, Имени или Отчества");
            }
            if (Objects.nonNull(birthDate)) {
                try {
                    Date date = format.parse(birthDate);
                } catch (ParseException e) {
                    throw new ParseException("Неверный формат даты рождения", e.getErrorOffset());
                }
            } else {
                throw new InvalidDataException("Неверный формат даты рождения");
            }
            if (Objects.isNull(phoneNumber)) {
                throw new InvalidDataException("Неверный формат номера телефона");
            }
            if (Objects.isNull(gender)) {
                throw new InvalidDataException("Неверный формат пола");
            }
            String fileName = lastName + ".txt";
            File file = new File(fileName);
            try (FileWriter fileWriter = new FileWriter(file, true)) {
                if (file.length() > 0) {
                    fileWriter.write('\n');
                }
                fileWriter.write(String.format("%s %s %s %s %s %s", lastName, firstName, patronymicName, birthDate, phoneNumber, gender));
            } catch (IOException e) {
                System.out.println("Ошибка при записи в файл:");
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка при работе с консолью");
        }
    }
}