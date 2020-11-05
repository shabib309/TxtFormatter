#include <iostream>
#include <fstream>
#include <string>
#include <list>
#include <windows.h>
#include <sstream>
#include <regex>
#include <dirent.h>
#include <chrono>

using namespace std;

list<string> readDict(string fileName)
{
    // Copied from:
    // https://gist.github.com/stevedoyle/1319089
    list<string> temp;
    string line;
    ifstream myfile(fileName);
    if (myfile.is_open())
    {
        while (myfile.good())
        {
            getline(myfile, line);
            temp.push_back(line);
        }
        myfile.close();
    }
    return temp;
}

string replaceAll(std::string &str, const std::string &from, const std::string &to)
{
    // Copied from:
    // https://stackoverflow.com/a/3418285/10821617
    size_t start_pos = 0;
    while ((start_pos = str.find(from, start_pos)) != std::string::npos)
    {
        str.replace(start_pos, from.length(), to);
        start_pos += to.length(); // In case 'to' contains 'from', like replacing 'x' with 'yx'
    }

    return str;
}

list<string> bad_words = readDict(".\\resources\\bad_words.txt");
list<string> good_words = readDict(".\\resources\\good_words.txt");
list<string> function_names = readDict(".\\resources\\function_names.txt");

void processFile(string fileName)
{
    string line;
    ifstream myfile(fileName);
    stringstream os;
    bool badWordFound = false;
    if (myfile.is_open())
    {
        string line;
        while (getline(myfile, line))
        {
            os << line << endl;
        }
    }
    string temp = os.str();
    temp = temp.substr(0, temp.size() - 1);
    for (string bad_word : bad_words)
    {
        if (temp.find(bad_word) != string::npos)
        {
            for (string good_word : good_words)
            {
                string good_word_for_check = good_word;
                transform(bad_word.begin(), bad_word.end(), bad_word.begin(), ::toupper);
                transform(good_word_for_check.begin(), good_word_for_check.end(), good_word_for_check.begin(), ::toupper);
                if (bad_word == good_word_for_check)
                {
                    badWordFound = true;
                    temp = replaceAll(temp, bad_word, good_word);
                    break;
                }
            }
        }
    }
    for (string function_name : function_names)
    {
        string search_function = function_name + ";";
        if (temp.find(search_function.c_str()) != string::npos)
        {
            for (string good_word : good_words)
            {
                if (function_name == good_word)
                {
                    badWordFound = true;
                    string new_function = good_word + "();";
                    temp = replaceAll(temp, search_function.c_str(), new_function);
                }
            }
        }
    }
    // Copied from:
    // https://stackoverflow.com/questions/11508798/conditionally-replace-regex-matches-in-string
    string temp_for_check = temp;
    temp = regex_replace(temp, regex("\\)\n\\s*\\{\n\\s*\\}"), ") { }");
    if (temp != temp_for_check)
    {
        badWordFound = true;
    }
    if (badWordFound)
    {
        ofstream write;
        write.open(fileName, ios::out | ios::binary);
        write << temp;
        write.close();
    }
}

void processFolder(string folder)
{
    // Copied from:
    // https://stackoverflow.com/questions/67273/how-do-you-iterate-through-every-file-directory-recursively-in-standard-c
    string process_path = folder + "\\Objects\\";
    string search_path = folder + "\\Objects\\*";
    WIN32_FIND_DATA fd;
    HANDLE hFind = ::FindFirstFile(search_path.c_str(), &fd);
    if (hFind != INVALID_HANDLE_VALUE)
    {
        do
        {
            if (!(fd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY))
            {
                string temp = process_path + fd.cFileName;
                processFile(temp.c_str());
            }
        } while (::FindNextFile(hFind, &fd));
        ::FindClose(hFind);
    }
}

int main(int argc, char const *argv[])
{
    string s(argv[1]);
    auto t1 = std::chrono::high_resolution_clock::now();
    processFolder(s);
    auto t2 = std::chrono::high_resolution_clock::now();
    auto duration = std::chrono::duration_cast<std::chrono::seconds>(t2 - t1).count();
    std::cout << "\nExecution-Time: " << duration << " seconds.\n"
              << endl;
    return 0;
}