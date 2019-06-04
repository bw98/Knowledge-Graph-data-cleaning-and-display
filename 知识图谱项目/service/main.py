import sys, os

proj_dir_path = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.append(proj_dir_path)

from service import ReadData
from Dao import Neo4jDao
from service import SaveDataByNeo4j
import time

org_profile_path = proj_dir_path + '/Data/OrgProfile_00.txt'
person_profile_path = proj_dir_path + '/Data/PersonProfile_00.txt'
test2_path = proj_dir_path + '/Data/test2_00.txt'
print(org_profile_path)

if __name__ == '__main__':
    org_profile_lines = ReadData.readLineByFile(file_name=org_profile_path)
    person_lines = ReadData.readLineByFile(file_name=person_profile_path)
    paper_lines = ReadData.readLineByFile(file_name=test2_path)

    org_list, person_list, paper_list = ReadData.hbaseDataProcessing(org_profile_lines=org_profile_lines,
                                                                     person_lines=person_lines,
                                                                     paper_lines=paper_lines)


    dao = Neo4jDao.Neo4jDao(username='neo4j', password='123456789')

    # 下面三个方法分别是存储org_profile、person_profile、test2的数据到neo4j
    SaveDataByNeo4j.saveOrg(dao=dao, org_list=org_list)
    #SaveDataByNeo4j.savePerson(dao=dao, person_list=person_list)
    #SaveDataByNeo4j.savePaper(dao=dao, paper_list=paper_list)
    debug = 2
